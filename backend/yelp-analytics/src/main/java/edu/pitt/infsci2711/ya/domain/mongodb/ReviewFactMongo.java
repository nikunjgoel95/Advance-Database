package edu.pitt.infsci2711.ya.domain.mongodb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "review_fact")
public class ReviewFactMongo {

    @Field("user_id")
    private String user_id;

    @Field("business_id")
    private Integer business_id;

    @Field("location_id")
    private Integer location_id;

    @Field("date_id")
    private Integer date_id;

    private Integer stars;
}
