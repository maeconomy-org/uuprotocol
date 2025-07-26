package io.recheck.uuidprotocol.domain.aggregate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AggregateUUFile extends AggregateNode {

    private String fileName;
    private String fileReference;
    private String label;
    private String contentType;
    private long size;

}
