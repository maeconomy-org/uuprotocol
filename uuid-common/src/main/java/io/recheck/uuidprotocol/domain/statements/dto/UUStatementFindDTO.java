package io.recheck.uuidprotocol.domain.statements.dto;

import io.recheck.uuidprotocol.domain.registrar.model.UUIDRegExp;
import io.recheck.uuidprotocol.domain.statements.model.UUStatementPredicate;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UUStatementFindDTO {

    @Pattern(regexp = UUIDRegExp.re)
    private String subject;

    private UUStatementPredicate predicate;

    @Pattern(regexp = UUIDRegExp.re)
    private String object;

    private Boolean softDeleted;

}
