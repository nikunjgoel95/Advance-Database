package edu.pitt.infsci2711.ya.domain.mysql;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "business")
public class BusinessMysql {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "business_id")
    private Integer businessId;

    @Column(name = "name")
    private String name;

    @Column(name = "categories")
    private String categories;
}
