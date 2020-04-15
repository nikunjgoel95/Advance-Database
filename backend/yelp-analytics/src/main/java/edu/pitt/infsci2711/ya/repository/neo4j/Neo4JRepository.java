package edu.pitt.infsci2711.ya.repository.neo4j;

import edu.pitt.infsci2711.ya.domain.neo4j.BusinessNeo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface Neo4JRepository extends CrudRepository<BusinessNeo, Long> {

    @Query("MATCH (u: User)-[:FRIENDS]->(friend: User) " +
            "RETURN u.name as name, size(collect(friend.name)) AS numberOfFriends " +
            "Order by numberOfFriends DESC " +
            "LIMIT 25")
    List<Map<String, Object>> getMostFriendlyUsers();

    @Query("MATCH (c:Category)<-[:IN_CATEGORY]-(b:Business) " +
            "WHERE LOWER(c.name) CONTAINS {0} " +
            "MATCH (b)<-[:REVIEWS]-(r:Review)<-[:WROTE]-(friend:User)-[:FRIENDS]-(me :User) " +
            "WHERE toFloat(r.stars) > 3.5 AND LOWER(me.name) = {1} " +
            "RETURN DISTINCT me.name,friend.name,b.name limit 15")
    List<Map<String, Object>> getSameLiking(String category, String name);

    @Query("MATCH (b: Business {name: {0}})<-[:REVIEWS]-(r:Review)<-[:WROTE]-(u:User) " +
            "return u.user_id, u.name,size(collect(r.review_id)) AS num_of_reviews " +
            "Order by num_of_reviews DESC " +
            "LIMIT 10")
    List<Map<String, Object>> getUserMostReviewed(String businessName);
}
