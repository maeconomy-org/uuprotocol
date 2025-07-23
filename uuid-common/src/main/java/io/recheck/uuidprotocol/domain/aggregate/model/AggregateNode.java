package io.recheck.uuidprotocol.domain.aggregate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AggregateNode extends AggregateAudit {

    private String uuid;

}
