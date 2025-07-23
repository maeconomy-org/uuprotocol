package io.recheck.uuidprotocol.domain.aggregate.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AggregatePropertyValue extends AggregatedNode {

    private String value;
    private String valueTypeCast;
    private String sourceType;
    List<AggregateFile> files = new ArrayList<>();

}
