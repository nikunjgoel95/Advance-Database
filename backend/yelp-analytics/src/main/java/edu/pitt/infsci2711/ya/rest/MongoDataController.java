package edu.pitt.infsci2711.ya.rest;

import edu.pitt.infsci2711.ya.domain.mongodb.*;
import edu.pitt.infsci2711.ya.repository.mongodb.BusinessMongoRepository;
import edu.pitt.infsci2711.ya.repository.mongodb.ReviewMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@RestController
@RequestMapping("/mongo")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MongoDataController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/bestBusiness")
    public Map<String, Object> getBestBusiness(
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "city", required = false) String city
    ) {
        city = city == null ? "Pittsburgh" : city;
        category = category == null ? "Restaurant" : category;

        long time = System.currentTimeMillis();
        LookupOperation businessLookup = LookupOperation.newLookup()
                .from("business")
                .localField("business_id")
                .foreignField("business_id")
                .as("business_data");

        LookupOperation locationLookup = LookupOperation.newLookup()
                .from("location")
                .localField("location_id")
                .foreignField("location_id")
                .as("location_data");

        GroupOperation groupBy = group("business_id").avg("stars").as("stars")
                .first("business_data").as("business")
                .first("location_data").as("location");
        SortOperation sortBy = Aggregation.sort(Sort.by(new Sort.Order(Direction.DESC, "stars")));
        LimitOperation limit = Aggregation.limit(5);
        Criteria cityCriteria = Criteria.where("location_data.city").is(city);
        Criteria categoryCriteria = Criteria.where("business_data.categories").regex(category);

        Aggregation aggregation = Aggregation.newAggregation(businessLookup, locationLookup, match(cityCriteria),
                match(categoryCriteria), groupBy, sortBy, limit);
        List<BestBusiness> results = mongoTemplate.aggregate(aggregation, ReviewFactMongo.class,
                BestBusiness.class).getMappedResults();

        List<Map<String, Object>> bestBusinesses = new ArrayList<>();
        for (BestBusiness bestBusiness: results) {
            Map<String, Object> bb = new HashMap<>();
            bb.put("businessName", bestBusiness.getBusiness().get(0).getName());
            bb.put("stars", bestBusiness.getStars());
            bb.put("city", bestBusiness.getLocation().get(0).getCity());
            bb.put("state", bestBusiness.getLocation().get(0).getState());
            bb.put("postalCode", bestBusiness.getLocation().get(0).getPostalCode());
            bestBusinesses.add(bb);
        }

        time = System.currentTimeMillis() - time;

        Map<String, Object> result = new HashMap<>();
        result.put("data", bestBusinesses);
        result.put("time", time);

        return result;
    }

    @GetMapping("/userMostTipped")
    public Map<String, Object> getUserTips2(
            @RequestParam(name = "category", required = false) String category
    ) {
        category = category == null ? "Restaurant" : category;
        Criteria criteria = Criteria.where("categories").regex(category);

        long time = System.currentTimeMillis();
        List<BusinessMongo> businesses = mongoTemplate.find(Query.query(criteria), BusinessMongo.class);

        Map<Integer, String> businessMap = new HashMap<>();
        List<Integer> businessIds = new ArrayList<>();
        List<String> userIds = new ArrayList<>();

        for (BusinessMongo bus : businesses) {
            businessMap.put(bus.getBusinessId(), bus.getName());
            businessIds.add(bus.getBusinessId());
        }

        criteria = Criteria.where("business_id").in(businessIds);
        GroupOperation groupBy = group("user_id").count().as("count")
                .first("user_id").as("user_id")
                .first("business_id").as("business_id");
        LimitOperation limit = limit(10);
        SortOperation sortby = sort(Sort.by(new Sort.Order(Direction.DESC, "count")));
        Aggregation aggregation = Aggregation.newAggregation(match(criteria), groupBy, sortby, limit);
        List<UserTips> userTips = mongoTemplate.aggregate(aggregation, "tips_fact", UserTips.class)
                .getMappedResults();

        for (UserTips ut : userTips) {
            userIds.add(ut.getUserId());
        }

        criteria = Criteria.where("user_id").in(userIds);

        List<UserMongo> userMongos = mongoTemplate.find(Query.query(criteria), UserMongo.class);
        Map<String, String> userNameMap = new HashMap<>();
        for (UserMongo user : userMongos) {
            userNameMap.put(user.getUserId(), user.getName());
        }

        for (int i = 0; i < userTips.size(); i++) {
            String businessName = businessMap.get(userTips.get(i).getBusinessId());
            String userName = userNameMap.get(userTips.get(i).getUserId());
            userTips.get(i).setUserName(userName);
            userTips.get(i).setBusinessName(businessName);
        }

        time = System.currentTimeMillis() - time;

        Map<String, Object> result = new HashMap<>();
        result.put("data", userTips);
        result.put("time", time);

        return result;
    }

    @GetMapping("/mostBusinessMonths")
    public List<Map> getMostBusinessMonths(
            @RequestParam(name = "state", required = false) String state
    ) {
        state = state == null ? "PA" : state;
        Criteria criteria = Criteria.where("state").is("PA");

        long time = System.currentTimeMillis();
        List<LocationMongo> locations = mongoTemplate.find(Query.query(criteria), LocationMongo.class);

        List<Integer> locationIds = new ArrayList<>();
        locations.forEach(x -> {locationIds.add(x.getLocationId());});

        criteria = Criteria.where("location_id").in(locationIds);
        LookupOperation dateLookup = LookupOperation.newLookup()
                .from("dates")
                .localField("date_id")
                .foreignField("date_id")
                .as("dates");
        GroupOperation groupBy = group("dates.month").count().as("count")
                .first("dates.month").as("month");
        List<ReviewFactMongo> reviews = mongoTemplate.find(Query.query(criteria), ReviewFactMongo.class);
        SortOperation sortBy = sort(Sort.by(new Sort.Order(Direction.DESC, "count")));

        Aggregation aggregation = Aggregation.newAggregation(match(criteria), dateLookup, groupBy, sortBy);
        List<Map> results = mongoTemplate.aggregate(aggregation, ReviewFactMongo.class,
                Map.class).getMappedResults();

        return results;
    }

    @GetMapping("/userMostReviewed")
    public Map<String, Object> getUserMostReviewed(
            @RequestParam(name = "businessName", required = false) String businessName
    ) {
        businessName = businessName != null ? businessName : "Starbucks";

        long time = System.currentTimeMillis();
        Criteria criteria = Criteria.where("name").is(businessName);
        List<BusinessMongo> business = mongoTemplate.find(Query.query(criteria), BusinessMongo.class);

        List<Integer> businessIds = new ArrayList<>();
        business.forEach(x -> {businessIds.add(x.getBusinessId());});

        System.out.println("Business count: " + businessIds.size());

        criteria = Criteria.where("business_id").in(businessIds);
        GroupOperation groupBy = group("user_id").count().as("review_count")
                .first("user_id").as("user_id");
        SortOperation sort = sort(Sort.by(new Sort.Order(Direction.DESC, "review_count")));
        LimitOperation limit = limit(10);
        Aggregation aggregation = Aggregation.newAggregation(match(criteria), groupBy, sort, limit);
        List<UserReview> userReviews = mongoTemplate.aggregate(aggregation, ReviewFactMongo.class,
                UserReview.class).getMappedResults();

        List<String> userIds = new ArrayList<>();
        Map<String, String> userNames = new HashMap<>();
        userReviews.forEach(x -> {userIds.add(x.getUserId());});

        criteria = Criteria.where("user_id").in(userIds);
        List<UserMongo> users = mongoTemplate.find(Query.query(criteria), UserMongo.class);
        users.forEach(x -> {userNames.put(x.getUserId(), x.getName());});

        userReviews.forEach(x -> {x.setUserName(userNames.get(x.getUserId()));});

        time = System.currentTimeMillis() - time;
        Map<String, Object> result = new HashMap<>();
        result.put("data", userReviews);
        result.put("time", time);

        return result;
    }
}
