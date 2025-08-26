package io.recheck.uuidprotocol.domain.aggregate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class AggregateUUPropertyValue extends AggregateNode {

    private String value;
    private String valueTypeCast;
    private String sourceType;
    private List<AggregateUUFile> files = new ArrayList<>();

}
