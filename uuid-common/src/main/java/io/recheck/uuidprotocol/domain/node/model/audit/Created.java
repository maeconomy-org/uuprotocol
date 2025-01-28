package io.recheck.uuidprotocol.domain.node.model.audit;

import java.time.Instant;

public interface Created {

    Instant getCreatedAt();

    String getCreatedBy();

}
