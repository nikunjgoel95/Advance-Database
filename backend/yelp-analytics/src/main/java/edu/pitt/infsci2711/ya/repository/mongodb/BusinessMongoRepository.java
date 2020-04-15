package edu.pitt.infsci2711.ya.repository.mongodb;

import edu.pitt.infsci2711.ya.domain.mongodb.BusinessMongo;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BusinessMongoRepository extends MongoRepository<BusinessMongo, String> {

}
