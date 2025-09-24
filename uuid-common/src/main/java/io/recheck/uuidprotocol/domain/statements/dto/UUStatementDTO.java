package io.recheck.uuidprotocol.domain.statements.dto;

import io.recheck.uuidprotocol.common.utils.BeanUtilsCommon;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRegExp;
import io.recheck.uuidprotocol.domain.statements.model.UUStatementPredicate;
import io.recheck.uuidprotocol.domain.statements.model.UUStatements;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UUStatementDTO {

    @NotBlank
    @Pattern(regexp = UUIDRegExp.re)
    private String subject;

    @NotNull
    private UUStatementPredicate predicate;

    @NotBlank
    @Pattern(regexp = UUIDRegExp.re)
    private String object;

    public UUStatements build() {
        UUStatements uuStatements = new UUStatements();
        BeanUtilsCommon.copyMatchingPropertiesDeep(this, uuStatements);
        return uuStatements;
    }

}
