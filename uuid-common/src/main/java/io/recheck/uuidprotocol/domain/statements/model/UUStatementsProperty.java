package io.recheck.uuidprotocol.domain.statements.model;

import lombok.Data;

import java.util.List;

@Data
public class UUStatementsProperty {

    private String key;
    private List<UUStatementsPropertyValue> values;

}
