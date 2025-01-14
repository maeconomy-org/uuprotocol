package io.recheck.uuidprotocol.nodenetwork.model.audit;

import java.time.Instant;

public interface Created {

    Instant getCreatedAt();

    String getCreatedBy();

}
