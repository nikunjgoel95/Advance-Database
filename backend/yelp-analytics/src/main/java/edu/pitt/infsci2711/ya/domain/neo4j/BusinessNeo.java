package edu.pitt.infsci2711.ya.domain.neo4j;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
@Getter
@Setter
public class BusinessNeo {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String categories;
}
