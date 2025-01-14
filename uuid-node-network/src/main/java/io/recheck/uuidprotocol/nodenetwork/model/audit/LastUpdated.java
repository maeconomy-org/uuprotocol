package io.recheck.uuidprotocol.nodenetwork.model.audit;

import java.time.Instant;

public interface LastUpdated {

    Instant getLastUpdatedAt();

    String getLastUpdatedBy();

}
