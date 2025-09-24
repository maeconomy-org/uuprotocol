package io.recheck.uuidprotocol.domain.audit;

import lombok.Data;

import java.time.Instant;

@Data
public class Audit {

    private Instant createdAt;
    private AuditUser createdBy;
    private Instant lastUpdatedAt;
    private AuditUser lastUpdatedBy;
    private Instant softDeletedAt;
    private AuditUser softDeleteBy;
    private Boolean softDeleted = false;

}
