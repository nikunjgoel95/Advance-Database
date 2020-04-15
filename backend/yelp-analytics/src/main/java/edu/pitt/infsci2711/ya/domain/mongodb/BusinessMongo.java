package edu.pitt.infsci2711.ya.domain.mongodb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "business")
public class BusinessMongo {

    @Field(name = "business_id")
    private Integer businessId;

    private String name;

    private String categories;
}
