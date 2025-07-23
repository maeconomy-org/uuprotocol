package io.recheck.uuidprotocol.domain.aggregate.model;

import lombok.Data;

import java.time.Instant;

@Data
public class AggregateAudit {

    private Instant createdAt;
    private String createdBy;
    private Instant lastUpdatedAt;
    private String lastUpdatedBy;
    private Instant softDeletedAt;
    private String softDeleteBy;
    private Boolean softDeleted = false;

}
