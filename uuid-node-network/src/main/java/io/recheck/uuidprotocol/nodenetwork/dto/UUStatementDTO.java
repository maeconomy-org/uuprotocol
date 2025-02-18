package io.recheck.uuidprotocol.nodenetwork.dto;

import io.recheck.uuidprotocol.nodenetwork.model.UUStatementPredicate;
import io.recheck.uuidprotocol.nodenetwork.model.UUStatements;
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

}
