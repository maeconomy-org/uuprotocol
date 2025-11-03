package io.recheck.uuidprotocol.domain.statements.model;

import io.recheck.uuidprotocol.domain.audit.Audit;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UUStatements extends Audit {

    private String subject;
    private UUStatementPredicate predicate;
    private String object;
    private List<UUStatementsProperty> properties;

    public UUStatements buildOpposite() {
        UUStatements uuStatementOpposite = new UUStatements();
        uuStatementOpposite.setSubject(getObject());
        uuStatementOpposite.setPredicate(UUStatementPredicate.getOpposite(getPredicate()));
        uuStatementOpposite.setObject(getSubject());
        uuStatementOpposite.setProperties(getProperties());
        return uuStatementOpposite;
    }

}
