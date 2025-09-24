package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener;

import io.recheck.uuidprotocol.domain.node.model.*;
import io.recheck.uuidprotocol.domain.statements.model.UUStatementPredicate;
import io.recheck.uuidprotocol.domain.statements.model.UUStatements;
import io.recheck.uuidprotocol.domain.statements.model.UUStatementsClassType;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.AggregateRepository;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.UUAddressCreate;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.UUNodeArrayDocCreate;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.UUNodeArrayUUIDAdd;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.abstracton.AbstractOperation;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.abstracton.AbstractOperationModelArray;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.NodeDataSource;
import io.recheck.uuidprotocol.nodenetwork.node.utils.UUNodeResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AggregateUUStatementsEventListener implements AggregateAuditEventListener<UUStatements> {

    private final UUNodeResolver uuNodeResolver;
    private final AggregateRepository aggregateRepository;

    static private MultiValueMap<UUStatementsClassType, AbstractOperation> statementsUpdateMap;

    static {
        statementsUpdateMap = new LinkedMultiValueMap<>();
        statementsUpdateMap.put(new UUStatementsClassType(UUObject.class, UUStatementPredicate.IS_PARENT_OF, UUObject.class),
                                                    List.of(new UUNodeArrayUUIDAdd<>("children")));
        statementsUpdateMap.put(new UUStatementsClassType(UUObject.class, UUStatementPredicate.IS_CHILD_OF, UUObject.class),
                                                    List.of(new UUNodeArrayUUIDAdd<>("parents")));
        statementsUpdateMap.put(new UUStatementsClassType(UUObject.class, UUStatementPredicate.IS_INPUT_OF, UUObject.class),
                                                    List.of(new UUNodeArrayUUIDAdd<>("outputs")));
        statementsUpdateMap.put(new UUStatementsClassType(UUObject.class, UUStatementPredicate.IS_OUTPUT_OF, UUObject.class),
                                                    List.of(new UUNodeArrayUUIDAdd<>("inputs")));
        statementsUpdateMap.put(new UUStatementsClassType(UUObject.class, UUStatementPredicate.IS_MODEL_OF, UUObject.class),
                                                    List.of(new UUNodeArrayUUIDAdd<>("models")));
        statementsUpdateMap.put(new UUStatementsClassType(UUObject.class, UUStatementPredicate.IS_INSTANCE_MODEL_OF, UUObject.class),
                                                    List.of(new UUNodeArrayUUIDAdd<>("instances")));


        statementsUpdateMap.put(new UUStatementsClassType(UUObject.class, UUStatementPredicate.HAS_PROPERTY, UUProperty.class),
                List.of(new UUNodeArrayDocCreate<>("properties")));
        statementsUpdateMap.put(new UUStatementsClassType(UUProperty.class, UUStatementPredicate.HAS_VALUE, UUPropertyValue.class),
                List.of(new UUNodeArrayDocCreate<>("properties.values")));
        statementsUpdateMap.put(new UUStatementsClassType(UUPropertyValue.class, UUStatementPredicate.HAS_FILE, UUFile.class),
                List.of(new UUNodeArrayDocCreate<>("properties.values.files")));
        statementsUpdateMap.put(new UUStatementsClassType(UUProperty.class, UUStatementPredicate.HAS_FILE, UUFile.class),
                List.of(new UUNodeArrayDocCreate<>("properties.files")));
        statementsUpdateMap.put(new UUStatementsClassType(UUObject.class, UUStatementPredicate.HAS_FILE, UUFile.class),
                List.of(new UUNodeArrayDocCreate<>("files")));

        statementsUpdateMap.put(new UUStatementsClassType(UUObject.class, UUStatementPredicate.HAS_ADDRESS, UUAddress.class),
                List.of(new UUAddressCreate()));
    }


    @Override
    public void postCreate(UUStatements uuStatement) {
        Node subjectNode = findNode(uuStatement.getSubject());
        Node objectNode = findNode(uuStatement.getObject());

        List<AbstractOperation> updateArrayValueList = statementsUpdateMap.get(new UUStatementsClassType(subjectNode.getClass(), uuStatement.getPredicate(), objectNode.getClass()));
        if (updateArrayValueList != null) {
            for (AbstractOperation operation : updateArrayValueList) {
                if (operation instanceof UUAddressCreate || operation instanceof UUNodeArrayUUIDAdd) {
                    Query query = operation.getQuery(subjectNode);
                    Update update = operation.getUpdate(objectNode);
                    aggregateRepository.update(query, update);
                }
                else if (operation instanceof UUNodeArrayDocCreate) {
                    Query query = operation.getQuery(subjectNode);
                    Update update = operation.getUpdate(new AbstractOperationModelArray<>(objectNode, subjectNode));
                    aggregateRepository.update(query, update);
                }
                else {
                    throw new UnsupportedOperationException("AggregateUUStatementsEventListener#postCreate");
                }


            }
        }

    }

    @Override
    public void postUpdate(UUStatements pojoAudit) {
        throw new UnsupportedOperationException("AggregateUUStatementsEventListener#postUpdate");
    }

    @Override
    public void postSoftDelete(UUStatements pojoAudit) {
        throw new UnsupportedOperationException("AggregateUUStatementsEventListener#postSoftDelete");
    }

    private <TNode extends Node> TNode findNode(String uuid) {
        Class<TNode> parentType = uuNodeResolver.getNodeClassTypeOfUUID(uuid);
        NodeDataSource<TNode> parentDataSource = uuNodeResolver.getNodeDataSourceForType(parentType);
        return parentDataSource.findLast(uuid);
    }

}
