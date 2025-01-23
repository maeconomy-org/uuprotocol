package io.recheck.uuidprotocol.nodenetwork.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UUProperty extends Node {

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

    private List<UUPropertyValue> propertyValues;

}
