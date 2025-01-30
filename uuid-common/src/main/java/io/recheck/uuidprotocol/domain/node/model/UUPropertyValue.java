package io.recheck.uuidprotocol.domain.node.model;

import lombok.Data;

import java.util.List;

@Data
public class UUPropertyValue {

    private String value;
    private List<String> uuFilesUUID;

    private String valueTypeCast;

    private String sourceType;

}
