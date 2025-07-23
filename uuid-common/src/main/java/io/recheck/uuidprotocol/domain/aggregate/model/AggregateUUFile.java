package io.recheck.uuidprotocol.domain.aggregate.model;

import lombok.Data;

@Data
public class AggregateUUFile extends AggregateNode {

    private String fileName;
    private String fileReference;
    private String label;

}
