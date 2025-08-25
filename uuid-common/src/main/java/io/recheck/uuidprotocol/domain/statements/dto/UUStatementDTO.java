package io.recheck.uuidprotocol.domain.statements.dto;

import io.recheck.uuidprotocol.domain.statements.model.UUStatementPredicate;
import io.recheck.uuidprotocol.domain.statements.model.UUStatements;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UUStatementDTO {

    @NotBlank
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
    private String subject;

    @NotNull
    private UUStatementPredicate predicate;

    @NotBlank
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
    private String object;

    public UUStatements build() {
        UUStatements uuStatements = new UUStatements();
        BeanUtils.copyProperties(this, uuStatements);
        return uuStatements;
    }

    public UUStatements buildOpposite() {
        UUStatements uuStatements = new UUStatements();

        uuStatements.setSubject(this.getObject());
        uuStatements.setPredicate(UUStatementPredicate.getOpposite(this.getPredicate()));
        uuStatements.setObject(this.getSubject());

        return uuStatements;
    }

}
