package edu.pitt.infsci2711.ya.domain.mongodb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "tips_fact")
public class TipsFactMongo {

    @Field("user_id")
    private String userId;

    @Field("business_id")
    private Integer businessId;

    @Field("location_id")
    private Integer locationId;

    @Field("date_id")
    private Integer dateId;

    private String text;

    @Field("compliment_count")
    private Integer complimentCount;
}
