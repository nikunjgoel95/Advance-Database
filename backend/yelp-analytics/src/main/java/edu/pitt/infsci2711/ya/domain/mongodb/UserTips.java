package edu.pitt.infsci2711.ya.domain.mongodb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserTips {

    @Field("user_id")
    private String userId;

    @Field("business_id")
    private Integer businessId;

    private Long count;

    private String userName;

    private String businessName;
}
