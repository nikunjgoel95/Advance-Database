package edu.pitt.infsci2711.ya.rest;

import edu.pitt.infsci2711.ya.repository.mysql.MysqlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mysql")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MysqlController {

    @Autowired
    private MysqlRepository mysqlRepository;

    @GetMapping("/mostReviewedBusiness/{category}")
    public Map<String, Object> getMostReviewedBusiness(
            @PathVariable("category") String category
    ) {
        long time = System.currentTimeMillis();
        List<Map<String, Object>> data = mysqlRepository.getMostReviewedBusiness(
                "%" + category.toLowerCase() + "%");
        time = System.currentTimeMillis() - time;
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("time", time);

        return result;
    }

    @GetMapping("/mostBusinessMonths/{state}")
    public Map<String, Object> getMostBusyMonths(
            @PathVariable("state") String state
    ) {
        long time = System.currentTimeMillis();
        List<Map<String, Object>> data = mysqlRepository.getMostBusyMonths(state);
        time = System.currentTimeMillis() - time;
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("time", time);

        return result;
    }

    @GetMapping("/userMostReviewed/{businessName}")
    public Map<String, Object> getUserMostReviewed(
            @PathVariable("businessName") String businesName
    ) {
        long time = System.currentTimeMillis();
        List<Map<String, Object>> data = mysqlRepository.getUserMostReviewed(businesName);
        time = System.currentTimeMillis() - time;
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("time", time);

        return result;
    }
}
