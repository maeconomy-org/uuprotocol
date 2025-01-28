package io.recheck.uuidprotocol.domain.node.model.audit;

import io.recheck.uuidprotocol.domain.node.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper=false)
public class Audit extends BaseEntity implements Created, LastUpdated, SoftDeleted {

    private Instant createdAt;
    private String createdBy;
    private Instant lastUpdatedAt;
    private String lastUpdatedBy;
    private Instant softDeletedAt;
    private String softDeleteBy;
    private Boolean softDeleted = false;

}
