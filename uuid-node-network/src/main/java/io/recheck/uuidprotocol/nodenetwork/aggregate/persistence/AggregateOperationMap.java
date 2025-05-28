package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence;

import io.recheck.uuidprotocol.domain.node.model.*;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operations.*;
import io.recheck.uuidprotocol.nodenetwork.statements.UUStatementsClass;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregateOperationMap {

    static private Map<UUStatementsClass, AbstractBinaryOperation> statementsPathMap;
    static private MultiValueMap<Class<?>, AbstractOperation> nodePathMap;

    static {
        nodePathMap = new LinkedMultiValueMap<>();

        nodePathMap.put(UUObject.class, List.of(new UpdateSetUUObject("")));
        nodePathMap.put(UUProperty.class, List.of(new UpdateSetArrayUUNode<UUProperty>("properties")));
        nodePathMap.put(UUPropertyValue.class, List.of(new UpdateSetArrayUUNode<UUPropertyValue>("properties.values")));
        nodePathMap.put(UUFile.class, List.of(new UpdateSetArrayUUNode<UUFile>("files"),
                new UpdateSetArrayUUNode<UUFile>("properties.files"),
                new UpdateSetArrayUUNode<UUFile>("properties.values.files")));
    }

    static {
        statementsPathMap = new HashMap<>();

        statementsPathMap.put(new UUStatementsClass(UUObject.class, UUStatementPredicate.IS_PARENT_OF, UUObject.class),
                new AbstractBinaryOperation(new UpdatePushUUObjectToArray("children"), new UpdatePullUUObjectFromArray("children")));

        statementsPathMap.put(new UUStatementsClass(UUObject.class, UUStatementPredicate.IS_CHILD_OF, UUObject.class),
                new AbstractBinaryOperation(new UpdatePushUUObjectToArray("parents"), new UpdatePullUUObjectFromArray("parents")));

        statementsPathMap.put(new UUStatementsClass(UUObject.class, UUStatementPredicate.IS_INPUT_OF, UUObject.class),
                new AbstractBinaryOperation(new UpdatePushUUObjectToArray("outputs"), new UpdatePullUUObjectFromArray("outputs")));
        statementsPathMap.put(new UUStatementsClass(UUObject.class, UUStatementPredicate.IS_OUTPUT_OF, UUObject.class),
                new AbstractBinaryOperation(new UpdatePushUUObjectToArray("inputs"), new UpdatePullUUObjectFromArray("inputs")));

        statementsPathMap.put(new UUStatementsClass(UUObject.class, UUStatementPredicate.IS_MODEL_OF, UUObject.class),
                new AbstractBinaryOperation(new UpdatePushUUObjectToArray("instances"), new UpdatePullUUObjectFromArray("instances")));
        statementsPathMap.put(new UUStatementsClass(UUObject.class, UUStatementPredicate.IS_INSTANCE_MODEL_OF, UUObject.class),
                new AbstractBinaryOperation(new UpdatePushUUObjectToArray("instances"), new UpdatePullUUObjectFromArray("instances")));


        statementsPathMap.put(new UUStatementsClass(UUObject.class, UUStatementPredicate.HAS_PROPERTY, UUProperty.class),
                new AbstractBinaryOperation(new UpdatePushUUNodeToArray<UUProperty, UUObject>("properties"), new UpdatePullUUNodeFromArray<UUProperty, UUObject>("properties")));

        statementsPathMap.put(new UUStatementsClass(UUProperty.class, UUStatementPredicate.HAS_VALUE, UUPropertyValue.class),
                new AbstractBinaryOperation(new UpdatePushUUNodeToArray<UUPropertyValue, UUProperty>("properties.values"), new UpdatePullUUNodeFromArray<UUProperty, UUObject>("properties.values")));

        statementsPathMap.put(new UUStatementsClass(UUPropertyValue.class, UUStatementPredicate.HAS_FILE, UUFile.class),
                new AbstractBinaryOperation(new UpdatePushUUNodeToArray<UUFile, UUPropertyValue>("properties.values.files"), new UpdatePullUUNodeFromArray<UUProperty, UUObject>("properties.values.files")));
        statementsPathMap.put(new UUStatementsClass(UUProperty.class, UUStatementPredicate.HAS_FILE, UUFile.class),
                new AbstractBinaryOperation(new UpdatePushUUNodeToArray<UUFile, UUProperty>("properties.files"), new UpdatePullUUNodeFromArray<UUProperty, UUObject>("properties.files")));
        statementsPathMap.put(new UUStatementsClass(UUObject.class, UUStatementPredicate.HAS_FILE, UUFile.class),
                new AbstractBinaryOperation(new UpdatePushUUNodeToArray<UUFile, UUObject>("files"), new UpdatePullUUNodeFromArray<UUProperty, UUObject>("files")));
    }

    static public AbstractBinaryOperation getStatementsPath(UUStatementsClass uuStatementsClass) {
        return statementsPathMap.get(uuStatementsClass);
    }

    static public List<AbstractOperation> getNodePath(Class uuNodeClass) {
        return nodePathMap.get(uuNodeClass);
    }
}
