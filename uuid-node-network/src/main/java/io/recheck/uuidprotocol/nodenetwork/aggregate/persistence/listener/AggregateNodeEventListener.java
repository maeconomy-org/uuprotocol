package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener;

import io.recheck.uuidprotocol.domain.node.model.*;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.AggregateRepository;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.UUAddressUpdate;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.UUNodeArrayDocUpdate;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.abstracton.AbstractOperation;
import io.recheck.uuidprotocol.nodenetwork.audit.AuditEventListener;
import io.recheck.uuidprotocol.nodenetwork.statements.UUStatementsDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

@RequiredArgsConstructor
public class AggregateNodeEventListener<TNode extends Node> implements AuditEventListener<TNode> {

    protected final UUStatementsDataSource uuStatementsDataSource;
    protected final AggregateRepository aggregateRepository;

    static private MultiValueMap<Class<?>, AbstractOperation> nodeUpdateArrayDocMap;

    static {
        nodeUpdateArrayDocMap = new LinkedMultiValueMap<>();

        nodeUpdateArrayDocMap.put(UUProperty.class,
                List.of(new UUNodeArrayDocUpdate<>("properties")));
        nodeUpdateArrayDocMap.put(UUPropertyValue.class,
                List.of(new UUNodeArrayDocUpdate<>("properties.values")));

        nodeUpdateArrayDocMap.put(UUFile.class,
                List.of(new UUNodeArrayDocUpdate<>("files"),
                new UUNodeArrayDocUpdate<>("properties.files"),
                new UUNodeArrayDocUpdate<>("properties.values.files")));

        nodeUpdateArrayDocMap.put(UUAddress.class,
                List.of(new UUAddressUpdate()));
    }

    @Override
    public void postCreate(TNode uuNode) {

    }

    @Override
    public void postUpdate(TNode uuNode) {
        if (uuStatementsDataSource.exist(uuNode.getUuid())) {
            List<AbstractOperation> updateArrayDocList = nodeUpdateArrayDocMap.get(uuNode.getClass());
            if (updateArrayDocList != null) {
                for (AbstractOperation operation : updateArrayDocList) {
                    aggregateRepository.update(operation.getQuery(uuNode), operation.getUpdate(uuNode));
                }
            }
        }
    }

    @Override
    public void postSoftDelete(TNode uuNode) {
        postUpdate(uuNode);
    }
}
