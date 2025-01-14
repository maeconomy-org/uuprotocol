package io.recheck.uuidprotocol.nodenetwork.model.audit;

import java.time.Instant;

public interface SoftDeleted {

    Boolean getSoftDeleted();

    Instant getSoftDeletedAt();

    String getSoftDeleteBy();


}
