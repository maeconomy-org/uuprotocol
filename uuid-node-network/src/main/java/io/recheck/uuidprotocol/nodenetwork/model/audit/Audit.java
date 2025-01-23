package io.recheck.uuidprotocol.nodenetwork.model.audit;

import io.recheck.uuidprotocol.nodenetwork.model.BaseEntity;
import lombok.Data;

import java.time.Instant;

@Data
public class Audit extends BaseEntity implements Created, LastUpdated, SoftDeleted {

    private Instant createdAt;
    private String createdBy;
    private Instant lastUpdatedAt;
    private String lastUpdatedBy;
    private Instant softDeletedAt;
    private String softDeleteBy;
    private Boolean softDeleted = false;

}
