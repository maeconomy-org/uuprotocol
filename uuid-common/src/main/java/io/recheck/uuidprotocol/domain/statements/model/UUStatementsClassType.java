package io.recheck.uuidprotocol.domain.statements.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UUStatementsClassType {

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
