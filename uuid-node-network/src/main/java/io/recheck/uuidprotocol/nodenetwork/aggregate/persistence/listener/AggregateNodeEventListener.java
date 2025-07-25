package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener;

import io.recheck.uuidprotocol.common.mongodb.query.UpdateArrayDoc;
import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.domain.node.model.UUFile;
import io.recheck.uuidprotocol.domain.node.model.UUProperty;
import io.recheck.uuidprotocol.domain.node.model.UUPropertyValue;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.AggregateRepository;
import io.recheck.uuidprotocol.nodenetwork.audit.AuditEventListener;
import io.recheck.uuidprotocol.nodenetwork.statements.UUStatementsDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

@RequiredArgsConstructor
public class AggregateNodeEventListener<TNode extends Node> implements AuditEventListener<TNode> {

    protected final UUStatementsDataSource uuStatementsDataSource;
    protected final AggregateRepository aggregateRepository;

    static private MultiValueMap<Class<?>, UpdateArrayDoc> nodeUpdateArrayDocMap;

    static {
        nodeUpdateArrayDocMap = new LinkedMultiValueMap<>();

        nodeUpdateArrayDocMap.put(UUProperty.class, List.of(new UpdateArrayDoc("properties", "uuid", "uuid")));
        nodeUpdateArrayDocMap.put(UUPropertyValue.class, List.of(new UpdateArrayDoc("properties.values", "uuid", "uuid")));
        nodeUpdateArrayDocMap.put(UUFile.class, List.of(new UpdateArrayDoc("files", "uuid", "uuid"),
                new UpdateArrayDoc("properties.files", "uuid", "uuid"),
                new UpdateArrayDoc("properties.values.files", "uuid", "uuid")));
    }

    @Override
    public void postCreate(TNode uuNode) {

    }

    @Override
    public void postUpdate(TNode uuNode) {
        if (uuStatementsDataSource.exist(uuNode.getUuid())) {
            List<UpdateArrayDoc> updateArrayDocList = nodeUpdateArrayDocMap.get(uuNode.getClass());
            if (updateArrayDocList != null) {
                for (UpdateArrayDoc updateArrayDoc : updateArrayDocList) {
                    Query query = new Query(Criteria.where(updateArrayDoc.getArrayDocFullPath() + ".uuid").is(uuNode.getUuid()));
                    aggregateRepository.update(query, updateArrayDoc.setArrayDoc(uuNode.getUuid(), uuNode));
                }
            }
        }
    }

    @Override
    public void postSoftDelete(TNode uuNode) {
        postUpdate(uuNode);
    }
}
