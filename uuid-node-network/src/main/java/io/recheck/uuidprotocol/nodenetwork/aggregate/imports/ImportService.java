package io.recheck.uuidprotocol.nodenetwork.aggregate.imports;

import io.recheck.uuidprotocol.common.firestore.FirestoreDataSourceBatch;
import io.recheck.uuidprotocol.domain.aggregate.model.*;
import io.recheck.uuidprotocol.domain.node.model.*;
import io.recheck.uuidprotocol.domain.node.model.audit.Audit;
import io.recheck.uuidprotocol.domain.owner.model.UUIDOwner;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.AggregateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportService {

    private final FirestoreDataSourceBatch firestoreDataSourceBatch;

    private final AggregateRepository aggregateRepository;

    public void saveAll(List<AggregateEntity> aggregateEntityList, String certFingerprint) {
        for (AggregateEntity aggObject : aggregateEntityList) {
            UUObject uuObject = saveUUObject(aggObject, certFingerprint);

            if (aggObject.getAddress() != null) {
                saveUUAddress(aggObject.getAddress(), uuObject.getUuid(), certFingerprint);
            }

            if (aggObject.getFiles() != null) {
                for (AggregateUUFile aggFile : aggObject.getFiles()) {
                    saveUUFile(aggFile, uuObject.getUuid(), certFingerprint);
                }
            }

            if (aggObject.getProperties() != null) {
                for (AggregateUUProperty aggProperty : aggObject.getProperties()) {
                    UUProperty uuProperty = saveUUProperty(aggProperty, uuObject.getUuid(), certFingerprint);

                    if (aggProperty.getFiles() != null) {
                        for (AggregateUUFile aggPropertyFile : aggProperty.getFiles()) {
                            saveUUFile(aggPropertyFile, uuProperty.getUuid(), certFingerprint);
                        }
                    }

                    if (aggProperty.getValues() != null) {
                        for (AggregateUUPropertyValue aggPropertyValue : aggProperty.getValues()) {
                            UUPropertyValue uuPropertyValue = saveUUPropertyValue(aggPropertyValue, uuProperty.getUuid(), certFingerprint);

                            if (aggPropertyValue.getFiles() != null) {
                                for (AggregateUUFile aggPropertyValueFile : aggPropertyValue.getFiles()) {
                                    saveUUFile(aggPropertyValueFile, uuPropertyValue.getUuid(), certFingerprint);
                                }
                            }
                        }
                    }

                }
            }
        }

        firestoreDataSourceBatch.commitBatch();
        aggregateRepository.saveAll(aggregateEntityList);
    }

    private void setAggNode(AggregateNode aggNode, Node uuNode) {
        aggNode.setUuid(uuNode.getUuid());
        aggNode.setCreatedAt(uuNode.getCreatedAt());
        aggNode.setCreatedBy(uuNode.getCreatedBy());
        aggNode.setLastUpdatedAt(uuNode.getLastUpdatedAt());
        aggNode.setLastUpdatedBy(uuNode.getLastUpdatedBy());
    }

    private UUAddress saveUUAddress(AggregateUUAddress aggAddress, String uuObjectUUID, String certFingerprint) {
        UUAddress uuAddress = new UUAddress();
        uuAddress.setUuid(createUUID(certFingerprint, UUAddress.class.getSimpleName()).getUuid());
        uuAddress.setFullAddress(aggAddress.getFullAddress());
        uuAddress.setStreet(aggAddress.getStreet());
        uuAddress.setHouseNumber(aggAddress.getHouseNumber());
        uuAddress.setCity(aggAddress.getCity());
        uuAddress.setPostalCode(aggAddress.getPostalCode());
        uuAddress.setCountry(aggAddress.getCountry());
        uuAddress.setState(aggAddress.getState());
        uuAddress.setDistrict(aggAddress.getDistrict());

        uuAddress = createAudit(uuAddress, certFingerprint);
        setAggNode(aggAddress, uuAddress);

        UUStatements uuStatement = new UUStatements();
        uuStatement.setSubject(uuObjectUUID);
        uuStatement.setPredicate(UUStatementPredicate.HAS_ADDRESS);
        uuStatement.setObject(uuAddress.getUuid());

        createAudit(uuStatement, certFingerprint);
        createAudit(buildOpposite(uuStatement), certFingerprint);

        return uuAddress;
    }

    private UUObject saveUUObject(AggregateEntity aggObject, String certFingerprint) {
        UUObject uuObject = new UUObject();
        uuObject.setUuid(createUUID(certFingerprint, UUObject.class.getSimpleName()).getUuid());
        uuObject.setName(aggObject.getName());
        uuObject.setAbbreviation(aggObject.getAbbreviation());
        uuObject.setVersion(aggObject.getVersion());
        uuObject.setDescription(aggObject.getDescription());

        uuObject = createAudit(uuObject, certFingerprint);
        setAggNode(aggObject, uuObject);
        return uuObject;
    }

    private UUFile saveUUFile(AggregateUUFile aggFile, String nodeUUID, String certFingerprint) {
        UUFile uuFile = new UUFile();
        uuFile.setUuid(createUUID(certFingerprint, UUFile.class.getSimpleName()).getUuid());
        uuFile.setFileReference(aggFile.getFileReference());
        uuFile.setFileName(aggFile.getFileName());
        uuFile.setLabel(aggFile.getLabel());

        uuFile = createAudit(uuFile, certFingerprint);
        setAggNode(aggFile, uuFile);

        UUStatements uuStatement = new UUStatements();
        uuStatement.setSubject(nodeUUID);
        uuStatement.setPredicate(UUStatementPredicate.HAS_FILE);
        uuStatement.setObject(uuFile.getUuid());

        createAudit(uuStatement, certFingerprint);
        createAudit(buildOpposite(uuStatement), certFingerprint);

        return uuFile;
    }

    private UUProperty saveUUProperty(AggregateUUProperty aggProperty, String uuObjectUUID, String certFingerprint) {
        UUProperty uuProperty = new UUProperty();
        uuProperty.setUuid(createUUID(certFingerprint, UUProperty.class.getSimpleName()).getUuid());
        uuProperty.setKey(aggProperty.getKey());
        uuProperty.setVersion(aggProperty.getVersion());
        uuProperty.setLabel(aggProperty.getLabel());
        uuProperty.setDescription(aggProperty.getDescription());
        uuProperty.setType(aggProperty.getType());
        uuProperty.setInputType(aggProperty.getInputType());
        uuProperty.setFormula(aggProperty.getFormula());
        uuProperty.setInputOrderPosition(aggProperty.getInputOrderPosition());
        uuProperty.setProcessingOrderPosition(aggProperty.getProcessingOrderPosition());
        uuProperty.setViewOrderPosition(aggProperty.getViewOrderPosition());

        uuProperty = createAudit(uuProperty, certFingerprint);
        setAggNode(aggProperty, uuProperty);

        UUStatements uuPropertyStatement = new UUStatements();
        uuPropertyStatement.setSubject(uuObjectUUID);
        uuPropertyStatement.setPredicate(UUStatementPredicate.HAS_PROPERTY);
        uuPropertyStatement.setObject(uuProperty.getUuid());

        createAudit(uuPropertyStatement, certFingerprint);
        createAudit(buildOpposite(uuPropertyStatement), certFingerprint);

        return uuProperty;
    }

    private UUPropertyValue saveUUPropertyValue(AggregateUUPropertyValue aggPropertyValue, String uuPropertyUUID, String certFingerprint) {
        UUPropertyValue uuPropertyValue = new UUPropertyValue();
        uuPropertyValue.setUuid(createUUID(certFingerprint, UUPropertyValue.class.getSimpleName()).getUuid());
        uuPropertyValue.setValue(aggPropertyValue.getValue());
        uuPropertyValue.setValueTypeCast(aggPropertyValue.getValueTypeCast());
        uuPropertyValue.setSourceType(aggPropertyValue.getSourceType());

        uuPropertyValue = createAudit(uuPropertyValue, certFingerprint);
        setAggNode(aggPropertyValue, uuPropertyValue);

        UUStatements uuStatement = new UUStatements();
        uuStatement.setSubject(uuPropertyUUID);
        uuStatement.setPredicate(UUStatementPredicate.HAS_VALUE);
        uuStatement.setObject(uuPropertyValue.getUuid());

        createAudit(uuStatement, certFingerprint);
        createAudit(buildOpposite(uuStatement), certFingerprint);

        return uuPropertyValue;
    }

    private UUStatements buildOpposite(UUStatements uuStatement) {
        UUStatements uuStatementOpposite = new UUStatements();
        uuStatementOpposite.setSubject(uuStatement.getObject());
        uuStatementOpposite.setPredicate(uuStatement.getPredicate().getOpposite(uuStatement.getPredicate()));
        uuStatementOpposite.setObject(uuStatement.getSubject());
        return uuStatementOpposite;
    }


    private <T extends Audit> T createAudit(T pojoAudit, String certFingerprint) {
        Instant now = Instant.now();
        pojoAudit.setCreatedAt(now);
        pojoAudit.setCreatedBy(certFingerprint);
        pojoAudit.setLastUpdatedAt(now);
        pojoAudit.setLastUpdatedBy(certFingerprint);
        return firestoreDataSourceBatch.createOrUpdate(pojoAudit);
    }

    private UUIDOwner createUUID(String certFingerprint, String nodeType) {
        String uuid = UUID.randomUUID().toString();
        UUIDOwner uuidOwner = new UUIDOwner(uuid, certFingerprint);
        uuidOwner.setNodeType(nodeType);
        return firestoreDataSourceBatch.createOrUpdate(uuidOwner);
    }

}
