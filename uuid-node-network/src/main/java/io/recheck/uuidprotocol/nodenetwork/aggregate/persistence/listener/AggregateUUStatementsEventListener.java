package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener;

import io.recheck.uuidprotocol.common.mongodb.query.UpdateArrayDoc;
import io.recheck.uuidprotocol.common.mongodb.query.UpdateArrayValue;
import io.recheck.uuidprotocol.domain.node.model.*;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.AggregateRepository;
import io.recheck.uuidprotocol.nodenetwork.audit.AuditEventListener;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.NodeDataSource;
import io.recheck.uuidprotocol.nodenetwork.node.utils.ClassResolver;
import io.recheck.uuidprotocol.nodenetwork.statements.UUStatementsClass;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AggregateUUStatementsEventListener implements AuditEventListener<UUStatements> {

    private final ClassResolver classResolver;
    private final AggregateRepository aggregateRepository;

    static private MultiValueMap<UUStatementsClass, UpdateArrayValue> statementsUpdateArrayValueMap;
    static private MultiValueMap<UUStatementsClass, UpdateArrayDoc> statementsUpdateArrayDocMap;

    static {
        statementsUpdateArrayValueMap = new LinkedMultiValueMap<>();
        statementsUpdateArrayValueMap.put(new UUStatementsClass(UUObject.class, UUStatementPredicate.IS_PARENT_OF, UUObject.class),
                                                    List.of(new UpdateArrayValue("children")));
        statementsUpdateArrayValueMap.put(new UUStatementsClass(UUObject.class, UUStatementPredicate.IS_CHILD_OF, UUObject.class),
                                                    List.of(new UpdateArrayValue("parents")));
        statementsUpdateArrayValueMap.put(new UUStatementsClass(UUObject.class, UUStatementPredicate.IS_INPUT_OF, UUObject.class),
                                                    List.of(new UpdateArrayValue("outputs")));
        statementsUpdateArrayValueMap.put(new UUStatementsClass(UUObject.class, UUStatementPredicate.IS_OUTPUT_OF, UUObject.class),
                                                    List.of(new UpdateArrayValue("inputs")));
        statementsUpdateArrayValueMap.put(new UUStatementsClass(UUObject.class, UUStatementPredicate.IS_MODEL_OF, UUObject.class),
                                                    List.of(new UpdateArrayValue("instances")));
        statementsUpdateArrayValueMap.put(new UUStatementsClass(UUObject.class, UUStatementPredicate.IS_INSTANCE_MODEL_OF, UUObject.class),
                                                    List.of(new UpdateArrayValue("instances")));


        statementsUpdateArrayDocMap = new LinkedMultiValueMap<>();
        statementsUpdateArrayDocMap.put(new UUStatementsClass(UUObject.class, UUStatementPredicate.HAS_PROPERTY, UUProperty.class),
                List.of(new UpdateArrayDoc("properties", "uuid", "uuid")));
        statementsUpdateArrayDocMap.put(new UUStatementsClass(UUProperty.class, UUStatementPredicate.HAS_VALUE, UUPropertyValue.class),
                List.of(new UpdateArrayDoc("properties.values", "uuid", "uuid")));
        statementsUpdateArrayDocMap.put(new UUStatementsClass(UUPropertyValue.class, UUStatementPredicate.HAS_FILE, UUFile.class),
                List.of(new UpdateArrayDoc("properties.values.files", "uuid", "uuid")));
        statementsUpdateArrayDocMap.put(new UUStatementsClass(UUProperty.class, UUStatementPredicate.HAS_FILE, UUFile.class),
                List.of(new UpdateArrayDoc("properties.files", "uuid", "uuid")));
        statementsUpdateArrayDocMap.put(new UUStatementsClass(UUObject.class, UUStatementPredicate.HAS_FILE, UUFile.class),
                List.of(new UpdateArrayDoc("files", "uuid", "uuid")));
    }


    @Override
    public void postCreate(UUStatements uuStatement) {
        Node subjectNode = findNode(uuStatement.getSubject());
        Node objectNode = findNode(uuStatement.getObject());

        List<UpdateArrayValue> updateArrayValueList = statementsUpdateArrayValueMap.get(new UUStatementsClass(subjectNode.getClass(), uuStatement.getPredicate(), objectNode.getClass()));
        if (updateArrayValueList != null) {
            for (UpdateArrayValue updateArrayValue : updateArrayValueList) {
                Query query = new Query(Criteria.where(updateArrayValue.getParentPath() + ".uuid").is(subjectNode.getUuid()));
                Update update = updateArrayValue.pushArrayValue(objectNode.getUuid());
                aggregateRepository.update(query, update);
            }
        }

        List<UpdateArrayDoc> updateArrayDocList = statementsUpdateArrayDocMap.get(new UUStatementsClass(subjectNode.getClass(), uuStatement.getPredicate(), objectNode.getClass()));
        if (updateArrayDocList != null) {
            for (UpdateArrayDoc updateArrayDoc : updateArrayDocList) {
                Query query = new Query(Criteria.where(updateArrayDoc.getParentPath() + ".uuid").is(subjectNode.getUuid()));
                Update update = updateArrayDoc.pushArrayDoc(subjectNode.getUuid(), objectNode);
                aggregateRepository.update(query, update);
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
        Class<TNode> parentType = classResolver.getNodeClassTypeOfUUID(uuid);
        NodeDataSource<TNode> parentDataSource = classResolver.getNodeDataSourceForType(parentType);
        return parentDataSource.findLast(uuid);
    }

}
