package io.recheck.uuidprotocol.domain.aggregate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AggregateUUObject extends AggregateNode {

    private String name;
    private String abbreviation;
    private String version;
    private String description;

}
