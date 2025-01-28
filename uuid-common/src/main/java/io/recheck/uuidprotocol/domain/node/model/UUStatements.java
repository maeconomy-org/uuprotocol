package io.recheck.uuidprotocol.domain.node.model;

import io.recheck.uuidprotocol.domain.node.model.audit.Audit;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UUStatements extends Audit {

    private String subject;
    private UUStatementPredicate predicate;
    private String object;

}
