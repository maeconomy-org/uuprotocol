package io.recheck.uuidprotocol.nodenetwork.aggregate;

import io.recheck.uuidprotocol.common.firestore.FirestoreDataSourceBatch;
import io.recheck.uuidprotocol.common.firestore.FirestoreDataSourceBatchModel;
import io.recheck.uuidprotocol.common.utils.BeanUtilsCommon;
import io.recheck.uuidprotocol.domain.aggregate.dto.create.AggregateEntityCreateDTO;
import io.recheck.uuidprotocol.domain.aggregate.model.*;
import io.recheck.uuidprotocol.domain.audit.Audit;
import io.recheck.uuidprotocol.domain.node.model.*;
import io.recheck.uuidprotocol.domain.owner.model.UUIDOwner;
import io.recheck.uuidprotocol.domain.statements.model.UUStatementPredicate;
import io.recheck.uuidprotocol.domain.statements.model.UUStatements;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.AggregateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregateService {

    private final FirestoreDataSourceBatch firestoreDataSourceBatch;

    private final AggregateRepository aggregateRepository;

    public AggregateEntity create(AggregateEntityCreateDTO aggregateEntityCreateDTO, String certFingerprint) {
        FirestoreDataSourceBatchModel batchModel = new FirestoreDataSourceBatchModel();
        AggregateEntity aggregateEntity = create(aggregateEntityCreateDTO, certFingerprint, batchModel);
        firestoreDataSourceBatch.commitBatch(batchModel);
        aggregateRepository.createMultiple(List.of(aggregateEntity));
        return aggregateEntity;
    }

    public void createMultiple(List<AggregateEntityCreateDTO> aggregateEntityCreateList, String certFingerprint) {
        FirestoreDataSourceBatchModel batchModel = new FirestoreDataSourceBatchModel();
        List<AggregateEntity> aggregateEntityList = new ArrayList<>();
        
        for (AggregateEntityCreateDTO aggregateEntityCreateDTO : aggregateEntityCreateList) {
            aggregateEntityList.add(create(aggregateEntityCreateDTO, certFingerprint, batchModel));
        }

        firestoreDataSourceBatch.commitBatch(batchModel);
        aggregateRepository.createMultiple(aggregateEntityList);
    }

    private AggregateEntity create(AggregateEntityCreateDTO aggregateEntityCreateDTO, String certFingerprint, FirestoreDataSourceBatchModel batchModel) {
        AggregateEntity aggregateEntity = new AggregateEntity();
        BeanUtilsCommon.copyMatchingPropertiesDeep(aggregateEntityCreateDTO, aggregateEntity);

        UUObject uuObject = createNode(aggregateEntity, UUObject.class, certFingerprint, batchModel);
        updateAggregateNode(aggregateEntity, uuObject);

        if (aggregateEntity.getAddress() != null) {
            UUAddress uuAddress = createNode(aggregateEntity.getAddress(), UUAddress.class, certFingerprint, batchModel);
            updateAggregateNode(aggregateEntity.getAddress(), uuAddress);
            createStatement(uuObject.getUuid(), UUStatementPredicate.HAS_ADDRESS, uuAddress.getUuid(), certFingerprint, batchModel);
        }

        if (aggregateEntity.getFiles() != null) {
            for (AggregateUUFile aggFile : aggregateEntity.getFiles()) {
                UUFile uuFile = createNode(aggFile, UUFile.class, certFingerprint, batchModel);
                updateAggregateNode(aggFile, uuFile);
                createStatement(uuObject.getUuid(), UUStatementPredicate.HAS_FILE, uuFile.getUuid(), certFingerprint, batchModel);
            }
        }

        if (aggregateEntity.getProperties() != null) {
            for (AggregateUUProperty aggProperty : aggregateEntity.getProperties()) {
                UUProperty uuProperty = createNode(aggProperty, UUProperty.class, certFingerprint, batchModel);
                updateAggregateNode(aggProperty, uuProperty);
                createStatement(uuObject.getUuid(), UUStatementPredicate.HAS_PROPERTY, uuProperty.getUuid(), certFingerprint, batchModel);

                if (aggProperty.getFiles() != null) {
                    for (AggregateUUFile aggPropertyFile : aggProperty.getFiles()) {
                        UUFile uuFile = createNode(aggPropertyFile, UUFile.class, certFingerprint, batchModel);
                        updateAggregateNode(aggPropertyFile, uuFile);
                        createStatement(uuProperty.getUuid(), UUStatementPredicate.HAS_FILE, uuFile.getUuid(), certFingerprint, batchModel);
                    }
                }

                if (aggProperty.getValues() != null) {
                    for (AggregateUUPropertyValue aggPropertyValue : aggProperty.getValues()) {
                        UUPropertyValue uuPropertyValue = createNode(aggPropertyValue, UUPropertyValue.class, certFingerprint, batchModel);
                        updateAggregateNode(aggPropertyValue, uuPropertyValue);
                        createStatement(uuProperty.getUuid(), UUStatementPredicate.HAS_VALUE, uuPropertyValue.getUuid(), certFingerprint, batchModel);

                        if (aggPropertyValue.getFiles() != null) {
                            for (AggregateUUFile aggPropertyValueFile : aggPropertyValue.getFiles()) {
                                UUFile uuFile = createNode(aggPropertyValueFile, UUFile.class, certFingerprint, batchModel);
                                updateAggregateNode(aggPropertyValueFile, uuFile);
                                createStatement(uuPropertyValue.getUuid(), UUStatementPredicate.HAS_FILE, uuFile.getUuid(), certFingerprint, batchModel);
                            }
                        }
                    }
                }

            }
        }

        return aggregateEntity;
    }

    private <T_AGG extends AggregateNode> void updateAggregateNode(T_AGG aggNode, Node uuNode) {
        aggNode.setUuid(uuNode.getUuid());
        aggNode.setCreatedAt(uuNode.getCreatedAt());
        aggNode.setCreatedBy(uuNode.getCreatedBy());
        aggNode.setLastUpdatedAt(uuNode.getLastUpdatedAt());
        aggNode.setLastUpdatedBy(uuNode.getLastUpdatedBy());
    }

    private <T_NODE extends Node, T_AGG extends AggregateNode> T_NODE createNode(T_AGG aggNode, Class<T_NODE> nodeClass, String certFingerprint, FirestoreDataSourceBatchModel batchModel) {
        T_NODE node = BeanUtils.instantiateClass(nodeClass);
        BeanUtilsCommon.copyMatchingPropertiesDeep(aggNode, node);
        node.setUuid(createUUID(certFingerprint, UUObject.class.getSimpleName(), batchModel).getUuid());

        node = createAudit(node, certFingerprint, batchModel);
        return node;
    }

    private void createStatement(String subject, UUStatementPredicate predicate, String object, String certFingerprint, FirestoreDataSourceBatchModel batchModel) {
        UUStatements uuStatement = new UUStatements();
        uuStatement.setSubject(subject);
        uuStatement.setPredicate(predicate);
        uuStatement.setObject(object);

        createAudit(uuStatement, certFingerprint, batchModel);
        createAudit(uuStatement.buildOpposite(), certFingerprint, batchModel);
    }

    private <T extends Audit> T createAudit(T pojoAudit, String certFingerprint, FirestoreDataSourceBatchModel batchModel) {
        Instant now = Instant.now();
        pojoAudit.setCreatedAt(now);
        pojoAudit.setCreatedBy(certFingerprint);
        pojoAudit.setLastUpdatedAt(now);
        pojoAudit.setLastUpdatedBy(certFingerprint);
        return firestoreDataSourceBatch.create(pojoAudit, batchModel);
    }

    private UUIDOwner createUUID(String certFingerprint, String nodeType, FirestoreDataSourceBatchModel batchModel) {
        String uuid = UUID.randomUUID().toString();
        UUIDOwner uuidOwner = new UUIDOwner(uuid, certFingerprint);
        uuidOwner.setNodeType(nodeType);
        return firestoreDataSourceBatch.create(uuidOwner, batchModel);
    }

}
