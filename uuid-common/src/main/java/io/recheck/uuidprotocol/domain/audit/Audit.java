package io.recheck.uuidprotocol.domain.audit;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper=false)
public class Audit {

    private Instant createdAt;
    private String createdBy;
    private Instant lastUpdatedAt;
    private String lastUpdatedBy;
    private Instant softDeletedAt;
    private String softDeleteBy;
    private Boolean softDeleted = false;

}
