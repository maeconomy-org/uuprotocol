package io.recheck.uuidprotocol.domain.aggregate.dto.create;

import lombok.Data;

@Data
public class AggregateUUFileCreateDTO {

    private String fileName;
    private String fileReference;
    private String label;
    private String contentType;
    private long size;

}
