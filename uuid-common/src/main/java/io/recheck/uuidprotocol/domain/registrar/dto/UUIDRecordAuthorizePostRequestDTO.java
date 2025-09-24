package io.recheck.uuidprotocol.domain.registrar.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.recheck.uuidprotocol.domain.registrar.model.PermissionType;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRegExp;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UUIDRecordAuthorizePostRequestDTO {

    @NotNull
    @Pattern(regexp = UUIDRegExp.re)
    private String userUuid;

//    @JsonIgnore
//    private String resourceType = "UUIDRecord";

    @NotNull
    @Pattern(regexp = UUIDRegExp.re)
    private String resourceId;


//    private PermissionType action;

}
