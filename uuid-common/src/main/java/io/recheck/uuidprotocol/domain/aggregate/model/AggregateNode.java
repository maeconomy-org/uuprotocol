package io.recheck.uuidprotocol.domain.aggregate.model;

import lombok.Data;

@Data
public class AggregateNode extends AggregateAudit {

    private String uuid;

}
