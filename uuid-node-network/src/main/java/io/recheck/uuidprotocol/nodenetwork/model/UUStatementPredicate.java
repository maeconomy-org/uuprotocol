package io.recheck.uuidprotocol.nodenetwork.model;

public enum UUStatementPredicate {

    IS_PARENT_OF("isParentOf"), IS_CHILD_OF("isChildOf"),
    IS_INPUT_OF("isInputOf"), IS_OUTPUT_OF("isOutputOf"),
    IS_MODEL_OF("isModelOf"), IS_INSTANCE_MODEL_OF("isInstanceModelOf"),
    IS_PROPERTY_OF("isPropertyOf"), HAS_PROPERTY("hasProperty");

    private String text;

    UUStatementPredicate(String text) {
        this.text = text;
    }

    public static UUStatementPredicate fromString(String text) {
        for (UUStatementPredicate b : UUStatementPredicate.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

}
