package io.recheck.uuidprotocol.domain.node.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.recheck.uuidprotocol.domain.audit.AuditUser;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRegExp;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeFindDTO {

    @Pattern(regexp = UUIDRegExp.re)
    private String uuid;

    private Boolean softDeleted;

    @JsonIgnore
    private AuditUser createdBy;

}
