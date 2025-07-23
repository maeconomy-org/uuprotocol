package io.recheck.uuidprotocol.domain.aggregate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class AggregateUUProperty extends AggregateNode {

    private String key;

    private String version;
    private String label;
    private String description;
    private String type;
    private String inputType;
    private String formula;
    private int inputOrderPosition;
    private int processingOrderPosition;
    private int viewOrderPosition;

    List<AggregateUUPropertyValue> values = new ArrayList<>();
    List<AggregateUUFile> files = new ArrayList<>();

}
