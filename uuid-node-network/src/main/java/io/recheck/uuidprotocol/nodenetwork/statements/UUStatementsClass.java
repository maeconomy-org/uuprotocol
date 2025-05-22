package io.recheck.uuidprotocol.nodenetwork.statements;

import io.recheck.uuidprotocol.domain.node.model.UUStatementPredicate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UUStatementsClass {

    private Class subjectClass;
    private UUStatementPredicate predicate;
    private Class objectClass;

    @Override
    public String toString() {
        return "{" +
                "subjectType=" + subjectClass.getSimpleName() +
                ", predicate=" + predicate +
                ", objectType=" + objectClass.getSimpleName() +
                '}';
    }

}
