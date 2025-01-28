package io.recheck.uuidprotocol.domain.node.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UUObject extends Node {

    private String name;

    private String abbreviation;

    private String version;

    private String description;

}
