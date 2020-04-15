package edu.pitt.infsci2711.ya.repository.mongodb;

import edu.pitt.infsci2711.ya.domain.mongodb.ReviewFactMongo;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Map;

public interface ReviewMongoRepository extends MongoRepository<ReviewFactMongo, String> {

    @Aggregation(pipeline = {"{ $lookup: {from: 'location', localField: 'location_id', foreignField: 'location_id', as: 'location' }}",
            /*"{ $unwind: {path: '$location'} }",
            "{ $project: { '_id': 0, 'business_id': 1, 'city': '$location.city', 'stars': 1 } }",
            "{ $match: { city: 'Pittsburgh'} } ",
            "{ $group: { _id: '$business_id', rating: { $avg: '$stars' } } } ",
            "{ $lookup: { from: 'business', localField: '_id', foreignField: 'business_id', as: 'business' } }",
            "{ $unwind: {path: '$business'} }",
            "{ $project: { '_id': 0, 'businessID': '$business.business_id', 'businessName': '$business.name', 'businessCategory': '$business.categories', 'rating': 1 } }",
            "{ $match: { $and: [{rating:{ $gt: 4.5, $lte: 5 }}, {'businessCategory': { $regex: '.*Restaurant.*'}}]}}",
            "{ $sort: {rating : -1, businessName : -1} }",*/
            "{ $limit : 5 }"})
    List<Map> getSuryasData();
}
