package io.recheck.uuidprotocol.domain.aggregate.model;

import lombok.Data;

@Data
public class AggregatedUUObject extends AggregatedNode {

    private String name;
    private String abbreviation;
    private String version;
    private String description;

}
