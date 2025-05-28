package io.recheck.uuidprotocol.domain.node.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.recheck.uuidprotocol.domain.node.model.UUStatementPredicate;
import io.recheck.uuidprotocol.domain.node.model.UUStatements;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UUStatementFindDTO {

    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
    private String subject;

    private UUStatementPredicate predicate;

    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
    private String object;

    private Boolean softDeleted;

    @JsonIgnore
    private String createdBy;

    public UUStatements build() {
        UUStatements uuStatements = new UUStatements();
        BeanUtils.copyProperties(this, uuStatements);
        return uuStatements;
    }

}
