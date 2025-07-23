package io.recheck.uuidprotocol.domain.aggregate.model;

import lombok.Data;

@Data
public class AggregatedNode extends AggregatedAudit {

    private String uuid;

}
