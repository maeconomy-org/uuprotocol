package io.recheck.uuidprotocol.domain.registrar.dto;

import io.recheck.uuidprotocol.domain.registrar.model.UUIDRegExp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UUIDRecordMetaPutRequestDTO {

    @Pattern(regexp = UUIDRegExp.re)
    private String uuid;

    @NotBlank
    private String nodeType;

}
