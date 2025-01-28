package io.recheck.uuidprotocol.domain.node.model.audit;

import java.time.Instant;

public interface SoftDeleted {

    Boolean getSoftDeleted();

    Instant getSoftDeletedAt();

    String getSoftDeleteBy();


}
