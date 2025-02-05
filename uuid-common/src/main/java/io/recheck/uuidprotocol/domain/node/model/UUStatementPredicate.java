package io.recheck.uuidprotocol.domain.node.model;

import java.util.Map;

public enum UUStatementPredicate {

    IS_PARENT_OF, IS_CHILD_OF,
    IS_INPUT_OF, IS_OUTPUT_OF,
    IS_MODEL_OF, IS_INSTANCE_MODEL_OF,
    IS_PROPERTY_OF, HAS_PROPERTY,
    IS_VALUE_OF, HAS_VALUE,
    IS_FILE_OF, HAS_FILE;

    private static Map<UUStatementPredicate, UUStatementPredicate> oppositePredicateOf = Map.ofEntries(
            Map.entry(UUStatementPredicate.IS_PARENT_OF, UUStatementPredicate.IS_CHILD_OF),
            Map.entry(UUStatementPredicate.IS_CHILD_OF, UUStatementPredicate.IS_PARENT_OF),

            Map.entry(UUStatementPredicate.IS_INPUT_OF, UUStatementPredicate.IS_OUTPUT_OF),
            Map.entry(UUStatementPredicate.IS_OUTPUT_OF, UUStatementPredicate.IS_INPUT_OF),

            Map.entry(UUStatementPredicate.IS_MODEL_OF, UUStatementPredicate.IS_INSTANCE_MODEL_OF),
            Map.entry(UUStatementPredicate.IS_INSTANCE_MODEL_OF, UUStatementPredicate.IS_MODEL_OF),

            Map.entry(UUStatementPredicate.IS_PROPERTY_OF, UUStatementPredicate.HAS_PROPERTY),
            Map.entry(UUStatementPredicate.HAS_PROPERTY, UUStatementPredicate.IS_PROPERTY_OF),

            Map.entry(UUStatementPredicate.IS_VALUE_OF, UUStatementPredicate.HAS_VALUE),
            Map.entry(UUStatementPredicate.HAS_VALUE, UUStatementPredicate.IS_VALUE_OF),

            Map.entry(UUStatementPredicate.IS_FILE_OF, UUStatementPredicate.HAS_FILE),
            Map.entry(UUStatementPredicate.HAS_FILE, UUStatementPredicate.IS_FILE_OF)
    );

    public UUStatementPredicate getOpposite(UUStatementPredicate uuStatementPredicate) {
        return oppositePredicateOf.get(uuStatementPredicate);
    }

}
