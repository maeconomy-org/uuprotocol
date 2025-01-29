package io.recheck.uuidprotocol.domain.node.model;

import java.util.Map;

public enum UUStatementPredicate {

    IS_PARENT_OF("isParentOf"), IS_CHILD_OF("isChildOf"),
    IS_INPUT_OF("isInputOf"), IS_OUTPUT_OF("isOutputOf"),
    IS_MODEL_OF("isModelOf"), IS_INSTANCE_MODEL_OF("isInstanceModelOf"),
    IS_PROPERTY_OF("isPropertyOf"), HAS_PROPERTY("hasProperty");

    private static Map<UUStatementPredicate, UUStatementPredicate> oppositePredicateOf = Map.of(
            UUStatementPredicate.IS_PARENT_OF, UUStatementPredicate.IS_CHILD_OF,
            UUStatementPredicate.IS_CHILD_OF, UUStatementPredicate.IS_PARENT_OF,

            UUStatementPredicate.IS_INPUT_OF, UUStatementPredicate.IS_OUTPUT_OF,
            UUStatementPredicate.IS_OUTPUT_OF, UUStatementPredicate.IS_INPUT_OF,

            UUStatementPredicate.IS_MODEL_OF, UUStatementPredicate.IS_INSTANCE_MODEL_OF,
            UUStatementPredicate.IS_INSTANCE_MODEL_OF, UUStatementPredicate.IS_MODEL_OF,

            UUStatementPredicate.IS_PROPERTY_OF, UUStatementPredicate.HAS_PROPERTY,
            UUStatementPredicate.HAS_PROPERTY, UUStatementPredicate.IS_PROPERTY_OF
    );

    private String text;

    UUStatementPredicate(String text) {
        this.text = text;
    }

    public UUStatementPredicate getOpposite(UUStatementPredicate uuStatementPredicate) {
        return oppositePredicateOf.get(uuStatementPredicate);
    }

}
