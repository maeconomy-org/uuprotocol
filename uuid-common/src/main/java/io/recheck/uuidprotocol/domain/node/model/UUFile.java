package io.recheck.uuidprotocol.domain.node.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UUFile extends Node {

    private String fileName;
    private String fileReference;
    private String label;

}
