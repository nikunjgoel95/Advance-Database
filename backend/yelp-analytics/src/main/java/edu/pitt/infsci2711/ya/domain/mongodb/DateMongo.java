package edu.pitt.infsci2711.ya.domain.mongodb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "dates")
public class DateMongo {

    @Field(name = "date_id")
    private Integer dateId;

    private Integer day;

    private Integer month;

    private Integer year;
}
