package io.recheck.uuidprotocol.domain.node.model.audit;

import java.time.Instant;

public interface LastUpdated {

    Instant getLastUpdatedAt();

    String getLastUpdatedBy();

}
