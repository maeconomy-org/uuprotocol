package io.recheck.uuidprotocol.common.firestore.index.model;

import lombok.Data;

import java.util.Map;

@Data
public class Operation {
    private String name;
    private IndexOperationMetadata metadata;
    private boolean done;
    private Map<String, String> error;
    private Map<String, String> response;
}
