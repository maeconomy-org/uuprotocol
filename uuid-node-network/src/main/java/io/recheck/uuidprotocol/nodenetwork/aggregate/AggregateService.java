package io.recheck.uuidprotocol.nodenetwork.aggregate;

import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.domain.node.model.UUObject;
import io.recheck.uuidprotocol.domain.node.model.UUStatements;
import io.recheck.uuidprotocol.nodenetwork.aggregate.model.AggregateEntity;
import io.recheck.uuidprotocol.nodenetwork.aggregate.model.AggregateFindDTO;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.AggregateOperationMap;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operations.AbstractBinaryOperation;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operations.AbstractOperation;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operations.UpdateSetArrayUUObject;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operations.UpdateUnsetUUObject;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.repository.AggregateRepository;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.repository.AggregateRepositoryTemplate;
import io.recheck.uuidprotocol.nodenetwork.common.ClassResolver;
import io.recheck.uuidprotocol.nodenetwork.datasource.NodeDataSource;
import io.recheck.uuidprotocol.nodenetwork.statements.UUStatementsClass;
import io.recheck.uuidprotocol.nodenetwork.statements.UUStatementsDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;
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

    public Page<AggregateEntity> find(AggregateFindDTO aggregateFindDTO) {
        return aggregateRepositoryTemplate.find(aggregateFindDTO);
    }

    public <TNode extends Node> void updateNode(TNode uuNode) {
        if (uuNode instanceof UUObject) {
            aggregateRepositoryTemplate.insertIfNotFound((UUObject) uuNode);
            List<AbstractOperation> operationList = AggregateOperationMap.getNodePath(uuNode.getClass());
            if (operationList!= null) {
                for (AbstractOperation operation : operationList) {
                    aggregateRepositoryTemplate.update(operation, uuNode);
                }
            }

            //delete history list
            aggregateRepositoryTemplate.update(new UpdateUnsetUUObject("history"), uuNode);
            //find all deleted , push history list
            NodeDataSource<UUObject> uuObjectNodeDataSource = classResolver.getNodeDataSourceForType(UUObject.class);
            List<UUObject> historyNodes = uuObjectNodeDataSource.findDeleted(uuNode.getUuid());
            for (UUObject historyNode : historyNodes) {
                aggregateRepositoryTemplate.update(new UpdateSetArrayUUObject("history"), uuNode, historyNode);
            }
        }
        else if (uuStatementsDataSource.exist(uuNode.getUuid())) {
            List<AbstractOperation> operationList = AggregateOperationMap.getNodePath(uuNode.getClass());
            if (operationList!= null) {
                for (AbstractOperation operation : operationList) {
                    aggregateRepositoryTemplate.update(operation, uuNode);
                }
            }
        }
    }

    public <TNode extends Node, VNode extends Node> void createStatement(UUStatements uuStatement) {
        Pair<TNode, VNode> nodes = getNodes(uuStatement);
        TNode parentNode = nodes.getFirst();
        VNode childNode = nodes.getSecond();

        AbstractBinaryOperation operation = AggregateOperationMap.getStatementsPath(new UUStatementsClass(parentNode.getClass(), uuStatement.getPredicate(), childNode.getClass()));
        if (operation != null) {
            aggregateRepositoryTemplate.update(operation.getCreateStatement(), parentNode, childNode);
        }

    }

    public <TNode extends Node, VNode extends Node> void deleteStatement(UUStatements uuStatement) {
        Pair<TNode, VNode> nodes = getNodes(uuStatement);
        TNode parentNode = nodes.getFirst();
        VNode childNode = nodes.getSecond();

        AbstractBinaryOperation operation = AggregateOperationMap.getStatementsPath(new UUStatementsClass(parentNode.getClass(), uuStatement.getPredicate(), childNode.getClass()));
        if (operation != null) {
            aggregateRepositoryTemplate.update(operation.getDeleteStatement(), parentNode, childNode);
        }

    }


    private <TNode extends Node, VNode extends Node> Pair<TNode, VNode> getNodes(UUStatements uuStatement) {
        Class<TNode> parentType = classResolver.getNodeClassTypeOfUUID(uuStatement.getSubject());
        Class<VNode> childType = classResolver.getNodeClassTypeOfUUID(uuStatement.getObject());

        NodeDataSource<TNode> parentDataSource = classResolver.getNodeDataSourceForType(parentType);
        NodeDataSource<VNode> childDataSource = classResolver.getNodeDataSourceForType(childType);

        TNode parentNode = parentDataSource.findLastUpdated(uuStatement.getSubject());
        if (parentNode == null) {
            parentNode = parentDataSource.findLastDeleted(uuStatement.getSubject());
        }

        VNode childNode = childDataSource.findLastUpdated(uuStatement.getObject());
        if (childNode == null) {
            childNode = childDataSource.findLastDeleted(uuStatement.getObject());
        }

        return Pair.of(parentNode, childNode);
    }

}
