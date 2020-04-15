package edu.pitt.infsci2711.ya.domain.mongodb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "location")
public class LocationMongo {

    @Field("location_id")
    private Integer locationId;

    private String city;

    private String state;

    @Field("postal_code")
    private String postalCode;
}
