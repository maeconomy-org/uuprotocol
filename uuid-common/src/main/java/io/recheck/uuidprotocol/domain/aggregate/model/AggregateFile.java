package io.recheck.uuidprotocol.domain.aggregate.model;

import lombok.Data;

@Data
public class AggregateFile extends AggregatedNode {

    private String fileName;
    private String fileReference;
    private String label;

}
