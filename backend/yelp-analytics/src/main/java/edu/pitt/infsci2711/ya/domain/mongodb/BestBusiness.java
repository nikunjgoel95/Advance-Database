package edu.pitt.infsci2711.ya.domain.mongodb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BestBusiness {

    private Double stars;

    private List<BusinessMongo> business;

    private List<LocationMongo> location;
}
