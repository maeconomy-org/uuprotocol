package io.recheck.uuidprotocol.domain.aggregate.model;

import lombok.Data;

@Data
public class AggregateUUObject extends AggregateNode {

    private String name;
    private String abbreviation;
    private String version;
    private String description;

}
