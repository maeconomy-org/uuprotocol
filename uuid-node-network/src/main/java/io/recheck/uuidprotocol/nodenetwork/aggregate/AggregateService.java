package io.recheck.uuidprotocol.nodenetwork.aggregate;

import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.domain.node.model.UUObject;
import io.recheck.uuidprotocol.domain.node.model.UUStatements;
import io.recheck.uuidprotocol.nodenetwork.aggregate.model.AggregateEntity;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.AggregateOperationMap;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operations.AbstractBinaryOperation;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operations.AbstractOperation;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.repository.AggregateRepository;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.repository.AggregateRepositoryTemplate;
import io.recheck.uuidprotocol.nodenetwork.common.ClassResolver;
import io.recheck.uuidprotocol.nodenetwork.datasource.NodeDataSource;
import io.recheck.uuidprotocol.nodenetwork.statements.UUStatementsClass;
import io.recheck.uuidprotocol.nodenetwork.statements.UUStatementsDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AggregateService {

    private final AggregateRepository aggregateRepository;
    private final AggregateRepositoryTemplate aggregateRepositoryTemplate;

    private final UUStatementsDataSource uuStatementsDataSource;
    private final ClassResolver classResolver;


    public List<AggregateEntity> findByAnyUuid(String uuid) {
        return aggregateRepository.findByAnyUuid(uuid.toLowerCase());
    }

    public Page<AggregateEntity> findByLastUpdatedAtDeepest(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return aggregateRepositoryTemplate.findByLastUpdatedAtDeepest(pageable);
    }

    public <TNode extends Node> void updateNode(TNode uuNode) {
        if (uuStatementsDataSource.findByUUID(uuNode.getUuid()) != null) {
            List<AbstractOperation> operationList = AggregateOperationMap.getNodePath(uuNode.getClass());
            if (operationList!= null) {
                for (AbstractOperation operation : operationList) {
                    aggregateRepositoryTemplate.updateNode(operation, uuNode);
                }
            }
        }
    }

    public <TNode extends Node, VNode extends Node> void createStatement(UUStatements uuStatement) {
        Class<TNode> parentType = classResolver.getNodeClassTypeOfUUID(uuStatement.getSubject());
        Class<VNode> childType = classResolver.getNodeClassTypeOfUUID(uuStatement.getObject());

        NodeDataSource<TNode> parentDataSource = classResolver.getNodeDataSourceForType(parentType);
        NodeDataSource<VNode> childDataSource = classResolver.getNodeDataSourceForType(childType);

        TNode parentNode = parentDataSource.findByUUID(uuStatement.getSubject());
        VNode childNode = childDataSource.findByUUID(uuStatement.getObject());

        if (parentNode instanceof UUObject) {
            aggregateRepositoryTemplate.insertIfNotFound((UUObject) parentNode);
        }
        if (childNode instanceof UUObject) {
            aggregateRepositoryTemplate.insertIfNotFound((UUObject) childNode);
        }

        AbstractBinaryOperation operation = AggregateOperationMap.getStatementsPath(new UUStatementsClass(parentType, uuStatement.getPredicate(), childType));
        if (operation != null) {
            aggregateRepositoryTemplate.updateStatement(operation.getCreateStatement(), parentNode, childNode);
        }

    }

    public <TNode extends Node, VNode extends Node> void deleteStatement(UUStatements uuStatement) {
        Class<TNode> parentType = classResolver.getNodeClassTypeOfUUID(uuStatement.getSubject());
        Class<VNode> childType = classResolver.getNodeClassTypeOfUUID(uuStatement.getObject());

        NodeDataSource<TNode> parentDataSource = classResolver.getNodeDataSourceForType(parentType);
        NodeDataSource<VNode> childDataSource = classResolver.getNodeDataSourceForType(childType);

        TNode parentNode = parentDataSource.findByUUID(uuStatement.getSubject());
        VNode childNode = childDataSource.findByUUID(uuStatement.getObject());

        AbstractBinaryOperation operation = AggregateOperationMap.getStatementsPath(new UUStatementsClass(parentType, uuStatement.getPredicate(), childType));
        if (operation != null) {
            aggregateRepositoryTemplate.updateStatement(operation.getDeleteStatement(), parentNode, childNode);
        }

    }

}
