package io.recheck.uuidprotocol.nodenetwork.model;

import io.recheck.uuidprotocol.nodenetwork.model.audit.Audit;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UUStatements extends Audit {

    private String subject;
    private UUStatementPredicate predicate;
    private String object;

}
