package io.recheck.uuidprotocol.domain.node.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UUPropertyValue extends Node {

    private String value;

    private String valueTypeCast;

    private String sourceType;

}
