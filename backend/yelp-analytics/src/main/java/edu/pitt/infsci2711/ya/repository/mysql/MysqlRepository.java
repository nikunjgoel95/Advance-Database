package edu.pitt.infsci2711.ya.repository.mysql;

import edu.pitt.infsci2711.ya.domain.mysql.BusinessMysql;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MysqlRepository extends CrudRepository<BusinessMysql, Integer> {

    @Query(value = "select * from adbm_analytical_level2.business limit 10", nativeQuery = true)
    List<BusinessMysql> getAllBusinessFromOtherDB();

    @Query(value = "select b.name, l.city, l.state, r.review_count, r.stars " +
            "from adbm_analytical_level2.business_location_review_fact r, adbm_analytical_level2.location l, " +
            "adbm_analytical_level2.business b " +
            "where r.business_id = b.business_id and l.location_id = r.location_id " +
            "and lower(b.categories) like ?1 and r.stars >= 4 " +
            "order by review_count desc, stars desc limit 10", nativeQuery = true)
    List<Map<String, Object>> getMostReviewedBusiness(String category);

    @Query(value = "select count(d.date_id) as cnt, d.month from review_fact r, business b, location l, dates d " +
            "where r.business_id = b.business_id and r.location_id = l.location_id " +
            "and d.date_id = r.date_id and l.state = ?1 " +
            "GROUP by d.month order by cnt desc", nativeQuery = true)
    List<Map<String, Object>> getMostBusyMonths(String state);
    
    @Query(value = "select u.name, review_count, stars  " +
            "from adbm_analytical_level2.business_review_fact r, adbm_analytical_level2.business b, " +
            "adbm_analytical_level2.user u  " +
            "where r.business_id = b.business_id " +
            "and r.user_id = u.user_id " +
            "and b.`name` = ?1 " +
            "order by review_count desc, name asc limit 10", nativeQuery = true)
    List<Map<String, Object>> getUserMostReviewed(String businessName);
}
