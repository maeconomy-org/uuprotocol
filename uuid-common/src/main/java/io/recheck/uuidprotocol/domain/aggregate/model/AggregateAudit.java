package io.recheck.uuidprotocol.domain.aggregate.model;

import io.recheck.uuidprotocol.domain.audit.AuditUser;
import lombok.Data;

import java.time.Instant;

@Data
public class AggregateAudit {

    private Instant createdAt;
    private AuditUser createdBy;
    private Instant lastUpdatedAt;
    private AuditUser lastUpdatedBy;
    private Instant softDeletedAt;
    private AuditUser softDeleteBy;
    private Boolean softDeleted = false;

}
