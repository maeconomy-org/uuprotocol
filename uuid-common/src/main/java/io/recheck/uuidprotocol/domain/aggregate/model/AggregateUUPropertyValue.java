package io.recheck.uuidprotocol.domain.aggregate.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AggregateUUPropertyValue extends AggregateNode {

    private String value;
    private String valueTypeCast;
    private String sourceType;
    List<AggregateUUFile> files = new ArrayList<>();

}
