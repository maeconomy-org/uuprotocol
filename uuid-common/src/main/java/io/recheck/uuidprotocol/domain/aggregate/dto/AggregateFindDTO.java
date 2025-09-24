package io.recheck.uuidprotocol.domain.aggregate.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.recheck.uuidprotocol.domain.audit.AuditUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregateFindDTO {

    private int page = 0;
    private int size = 5;

    @JsonIgnore
    private AuditUser createdBy;

    private Boolean hasChildrenFull = false;

    private Boolean hasHistory = false;

    private Boolean hasParentUUIDFilter = false;
    private String parentUUID;

    private String searchTerm;

}
