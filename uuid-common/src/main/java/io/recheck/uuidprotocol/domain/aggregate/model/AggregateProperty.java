package io.recheck.uuidprotocol.domain.aggregate.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AggregateProperty extends AggregatedNode {

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

    List<AggregatePropertyValue> values = new ArrayList<>();
    List<AggregateFile> files = new ArrayList<>();

}
