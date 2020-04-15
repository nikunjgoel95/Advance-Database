package edu.pitt.infsci2711.ya.rest;

import edu.pitt.infsci2711.ya.repository.neo4j.Neo4JRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/neo4j")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Neo4JDataController {

    @Autowired
    private Neo4JRepository neo4JRepository;

    @GetMapping("/friendlyUsers")
    public Map<String,Object> getMostFriendlyUsers() {
        long time = System.currentTimeMillis();
        List<Map<String, Object>> data = neo4JRepository.getMostFriendlyUsers();
        time = System.currentTimeMillis() - time;
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("time", time);

        return result;
    }

    @GetMapping("/sameLiking")
    public Map<String, Object> getSameLiking(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "userName", required = false) String userName
    ) {
        category = category != null ? category.toLowerCase() : "Breweries";
        userName = userName != null ? userName.toLowerCase() : "Stephanie";

        long time = System.currentTimeMillis();
        List<Map<String, Object>> data = neo4JRepository.getSameLiking(category, userName);
        time = System.currentTimeMillis() - time;
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("time", time);

        return result;
    }

    @GetMapping("/userMostReviewed")
    public Map<String, Object> getUserMostReviewed(
            @RequestParam(name = "businessName", required = false) String businessName
    ) {
        businessName = businessName != null ? businessName : "Starbucks";
        long time = System.currentTimeMillis();
        List<Map<String, Object>> data = neo4JRepository.getUserMostReviewed(businessName);
        time = System.currentTimeMillis() - time;
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("time", time);

        return result;
    }
}
