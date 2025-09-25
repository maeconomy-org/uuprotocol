package io.recheck.uuidprotocol.nodenetwork.aggregate;

import io.recheck.uuidprotocol.common.firestore.FirestoreDataSourceBatch;
import io.recheck.uuidprotocol.common.firestore.FirestoreDataSourceBatchModel;
import io.recheck.uuidprotocol.common.utils.BeanUtilsCommon;
import io.recheck.uuidprotocol.domain.aggregate.dto.create.AggregateCreateDTO;
import io.recheck.uuidprotocol.domain.aggregate.dto.create.AggregateEntityCreateDTO;
import io.recheck.uuidprotocol.domain.aggregate.model.*;
import io.recheck.uuidprotocol.domain.audit.Audit;
import io.recheck.uuidprotocol.domain.audit.AuditUser;
import io.recheck.uuidprotocol.domain.node.model.*;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRecord;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRecordMeta;
import io.recheck.uuidprotocol.domain.statements.model.UUStatementPredicate;
import io.recheck.uuidprotocol.domain.statements.model.UUStatements;
import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;
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

    public List<AggregateEntity> create(AggregateCreateDTO aggregateCreateDTO) {
        FirestoreDataSourceBatchModel batchModel = new FirestoreDataSourceBatchModel();
        List<AggregateEntity> aggregateEntityList = new ArrayList<>();

        for (AggregateEntityCreateDTO aggregateEntityCreateDTO : aggregateCreateDTO.getAggregateEntityList()) {
            aggregateEntityList.add(create(aggregateEntityCreateDTO, aggregateCreateDTO.getUser(), batchModel));
        }

        firestoreDataSourceBatch.commitBatch(batchModel);
        aggregateRepository.createMultiple(aggregateEntityList);

        return aggregateEntityList;
    }

    public void createMultiple(AggregateCreateDTO aggregateCreateDTO) {
        FirestoreDataSourceBatchModel batchModel = new FirestoreDataSourceBatchModel();
        List<AggregateEntity> aggregateEntityList = new ArrayList<>();
        
        for (AggregateEntityCreateDTO aggregateEntityCreateDTO : aggregateCreateDTO.getAggregateEntityList()) {
            aggregateEntityList.add(create(aggregateEntityCreateDTO, aggregateCreateDTO.getUser(), batchModel));
        }

        firestoreDataSourceBatch.commitBatch(batchModel);
        aggregateRepository.createMultiple(aggregateEntityList);
    }

    private AggregateEntity create(AggregateEntityCreateDTO aggregateEntityCreateDTO, UserDetailsCustom user, FirestoreDataSourceBatchModel batchModel) {
        AggregateEntity aggregateEntity = new AggregateEntity();
        BeanUtilsCommon.copyMatchingPropertiesDeep(aggregateEntityCreateDTO, aggregateEntity);

        UUObject uuObject = createNode(aggregateEntity, UUObject.class, user, batchModel);
        updateAggregateNode(aggregateEntity, uuObject);

        if (aggregateEntity.getAddress() != null) {
            UUAddress uuAddress = createNode(aggregateEntity.getAddress(), UUAddress.class, user, batchModel);
            updateAggregateNode(aggregateEntity.getAddress(), uuAddress);
            createStatement(uuObject.getUuid(), UUStatementPredicate.HAS_ADDRESS, uuAddress.getUuid(), user, batchModel);
        }

        if (aggregateEntity.getFiles() != null) {
            for (AggregateUUFile aggFile : aggregateEntity.getFiles()) {
                UUFile uuFile = createNode(aggFile, UUFile.class, user, batchModel);
                updateAggregateNode(aggFile, uuFile);
                createStatement(uuObject.getUuid(), UUStatementPredicate.HAS_FILE, uuFile.getUuid(), user, batchModel);
            }
        }

        if (aggregateEntity.getProperties() != null) {
            for (AggregateUUProperty aggProperty : aggregateEntity.getProperties()) {
                UUProperty uuProperty = createNode(aggProperty, UUProperty.class, user, batchModel);
                updateAggregateNode(aggProperty, uuProperty);
                createStatement(uuObject.getUuid(), UUStatementPredicate.HAS_PROPERTY, uuProperty.getUuid(), user, batchModel);

                if (aggProperty.getFiles() != null) {
                    for (AggregateUUFile aggPropertyFile : aggProperty.getFiles()) {
                        UUFile uuFile = createNode(aggPropertyFile, UUFile.class, user, batchModel);
                        updateAggregateNode(aggPropertyFile, uuFile);
                        createStatement(uuProperty.getUuid(), UUStatementPredicate.HAS_FILE, uuFile.getUuid(), user, batchModel);
                    }
                }

                if (aggProperty.getValues() != null) {
                    for (AggregateUUPropertyValue aggPropertyValue : aggProperty.getValues()) {
                        UUPropertyValue uuPropertyValue = createNode(aggPropertyValue, UUPropertyValue.class, user, batchModel);
                        updateAggregateNode(aggPropertyValue, uuPropertyValue);
                        createStatement(uuProperty.getUuid(), UUStatementPredicate.HAS_VALUE, uuPropertyValue.getUuid(), user, batchModel);

                        if (aggPropertyValue.getFiles() != null) {
                            for (AggregateUUFile aggPropertyValueFile : aggPropertyValue.getFiles()) {
                                UUFile uuFile = createNode(aggPropertyValueFile, UUFile.class, user, batchModel);
                                updateAggregateNode(aggPropertyValueFile, uuFile);
                                createStatement(uuPropertyValue.getUuid(), UUStatementPredicate.HAS_FILE, uuFile.getUuid(), user, batchModel);
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

    private <T_NODE extends Node, T_AGG extends AggregateNode> T_NODE createNode(T_AGG aggNode, Class<T_NODE> nodeClass, UserDetailsCustom user, FirestoreDataSourceBatchModel batchModel) {
        T_NODE node = BeanUtils.instantiateClass(nodeClass);
        BeanUtilsCommon.copyMatchingPropertiesDeep(aggNode, node);
        node.setUuid(createUUID(user, nodeClass.getSimpleName(), batchModel).getUuid());

        node = createAudit(node, user, batchModel);
        return node;
    }

    private void createStatement(String subject, UUStatementPredicate predicate, String object, UserDetailsCustom user, FirestoreDataSourceBatchModel batchModel) {
        UUStatements uuStatement = new UUStatements();
        uuStatement.setSubject(subject);
        uuStatement.setPredicate(predicate);
        uuStatement.setObject(object);

        createAudit(uuStatement, user, batchModel);
        createAudit(uuStatement.buildOpposite(), user, batchModel);
    }

    private <T extends Audit> T createAudit(T pojoAudit, UserDetailsCustom user, FirestoreDataSourceBatchModel batchModel) {
        Instant now = Instant.now();
        pojoAudit.setCreatedAt(now);
        pojoAudit.setCreatedBy(new AuditUser(user));
        pojoAudit.setLastUpdatedAt(now);
        pojoAudit.setLastUpdatedBy(new AuditUser(user));
        return firestoreDataSourceBatch.create(pojoAudit, batchModel);
    }

    private UUIDRecord createUUID(UserDetailsCustom user, String nodeType, FirestoreDataSourceBatchModel batchModel) {
        String uuid = UUID.randomUUID().toString();
        UUIDRecord uuidRecord = new UUIDRecord(uuid, user.getUserUuid(), new UUIDRecordMeta());
        uuidRecord.getUuidRecordMeta().setNodeType(nodeType);
        return createAudit(uuidRecord, user, batchModel);
    }

}
