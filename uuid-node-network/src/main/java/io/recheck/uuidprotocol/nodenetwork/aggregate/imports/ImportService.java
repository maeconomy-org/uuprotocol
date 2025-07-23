package io.recheck.uuidprotocol.nodenetwork.aggregate.imports;

import io.recheck.uuidprotocol.domain.aggregate.model.*;
import io.recheck.uuidprotocol.domain.node.model.*;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.AggregateRepository;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.*;
import io.recheck.uuidprotocol.nodenetwork.statements.UUStatementsDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportService {

    private final UUObjectDataSource uuObjectDataSource;
    private final UUPropertyDataSource uuPropertyDataSource;
    private final UUPropertyValueDataSource uuPropertyValueDataSource;
    private final UUFileDataSource uuFileDataSource;
    private final UUAddressDataSource uuAddressDataSource;
    private final UUStatementsDataSource uuStatementsDataSource;

    private final AggregateRepository aggregateRepository;

    private final UUIDOwnerImportService uuidOwnerImportService;




    public void saveAll(List<AggregateEntity> aggregateEntityList, String certFingerprint) {
        for (AggregateEntity aggObject : aggregateEntityList) {
            UUObject uuObject = saveUUObject(aggObject, certFingerprint);
            setAggNode(aggObject, uuObject);

            if (aggObject.getAddress() != null) {
                UUAddress uuAddress = saveUUAddress(aggObject.getAddress(), uuObject.getUuid(), certFingerprint);
                setAggNode(aggObject.getAddress(), uuAddress);
            }

            if (aggObject.getFiles() != null) {
                for (AggregateUUFile aggFile : aggObject.getFiles()) {
                    UUFile uuFile = saveUUFile(aggFile, uuObject.getUuid(), certFingerprint);
                    setAggNode(aggFile, uuFile);
                }
            }

            if (aggObject.getProperties() != null) {
                for (AggregateUUProperty aggProperty : aggObject.getProperties()) {
                    UUProperty uuProperty = saveUUProperty(aggProperty, uuObject.getUuid(), certFingerprint);
                    setAggNode(aggProperty, uuProperty);

                    if (aggProperty.getFiles() != null) {
                        for (AggregateUUFile aggPropertyFile : aggProperty.getFiles()) {
                            UUFile uuFile = saveUUFile(aggPropertyFile, uuProperty.getUuid(), certFingerprint);
                            setAggNode(aggPropertyFile, uuFile);
                        }
                    }

                    if (aggProperty.getValues() != null) {
                        for (AggregateUUPropertyValue aggPropertyValue : aggProperty.getValues()) {
                            UUPropertyValue uuPropertyValue = saveUUPropertyValue(aggPropertyValue, uuProperty.getUuid(), certFingerprint);
                            setAggNode(aggPropertyValue, uuPropertyValue);

                            if (aggPropertyValue.getFiles() != null) {
                                for (AggregateUUFile aggPropertyValueFile : aggPropertyValue.getFiles()) {
                                    UUFile uuFile = saveUUFile(aggPropertyValueFile, uuPropertyValue.getUuid(), certFingerprint);
                                    setAggNode(aggPropertyValueFile, uuFile);
                                }
                            }
                        }
                    }

                }
            }
        }

        aggregateRepository.saveAll(aggregateEntityList);
    }

    private void setAggNode(AggregateNode aggNode, Node uuNode) {
        aggNode.setUuid(uuNode.getUuid());
        aggNode.setCreatedAt(uuNode.getCreatedAt());
        aggNode.setCreatedBy(uuNode.getCreatedBy());
        aggNode.setLastUpdatedAt(uuNode.getLastUpdatedAt());
        aggNode.setLastUpdatedBy(uuNode.getLastUpdatedBy());
    }

    private UUAddress saveUUAddress(AggregateUUAddress aggAddress, String uuObjectStatementUUID, String certFingerprint) {
        UUAddress uuAddress = new UUAddress();
        uuAddress.setUuid(uuidOwnerImportService.createUUID(certFingerprint, UUAddress.class.getSimpleName()).getUuid());
        uuAddress.setFullAddress(aggAddress.getFullAddress());
        uuAddress.setStreet(aggAddress.getStreet());
        uuAddress.setHouseNumber(aggAddress.getHouseNumber());
        uuAddress.setCity(aggAddress.getCity());
        uuAddress.setPostalCode(aggAddress.getPostalCode());
        uuAddress.setCountry(aggAddress.getCountry());
        uuAddress.setState(aggAddress.getState());
        uuAddress.setDistrict(aggAddress.getDistrict());

        uuAddress = uuAddressDataSource.createAudit(uuAddress, certFingerprint);

        UUStatements uuStatement = new UUStatements();
        uuStatement.setSubject(uuObjectStatementUUID);
        uuStatement.setPredicate(UUStatementPredicate.HAS_ADDRESS);
        uuStatement.setObject(uuAddress.getUuid());

        uuStatementsDataSource.createAudit(uuStatement, certFingerprint);
        uuStatementsDataSource.createAudit(buildOpposite(uuStatement), certFingerprint);

        return uuAddress;
    }

    private UUObject saveUUObject(AggregateEntity aggObject, String certFingerprint) {
        UUObject uuObject = new UUObject();
        uuObject.setUuid(uuidOwnerImportService.createUUID(certFingerprint, UUObject.class.getSimpleName()).getUuid());
        uuObject.setName(aggObject.getName());
        uuObject.setAbbreviation(aggObject.getAbbreviation());
        uuObject.setVersion(aggObject.getVersion());
        uuObject.setDescription(aggObject.getDescription());

        return uuObjectDataSource.createAudit(uuObject, certFingerprint);
    }

    private UUFile saveUUFile(AggregateUUFile aggFile, String nodeStatementUUID, String certFingerprint) {
        UUFile uuFile = new UUFile();
        uuFile.setUuid(uuidOwnerImportService.createUUID(certFingerprint, UUFile.class.getSimpleName()).getUuid());
        uuFile.setFileReference(aggFile.getFileReference());
        uuFile.setFileName(aggFile.getFileName());
        uuFile.setLabel(aggFile.getLabel());

        uuFile = uuFileDataSource.createAudit(uuFile, certFingerprint);

        UUStatements uuStatement = new UUStatements();
        uuStatement.setSubject(nodeStatementUUID);
        uuStatement.setPredicate(UUStatementPredicate.HAS_FILE);
        uuStatement.setObject(uuFile.getUuid());

        uuStatementsDataSource.createAudit(uuStatement, certFingerprint);
        uuStatementsDataSource.createAudit(buildOpposite(uuStatement), certFingerprint);

        return uuFile;
    }

    private UUProperty saveUUProperty(AggregateUUProperty aggProperty, String uuObjectStatementUUID, String certFingerprint) {
        UUProperty uuProperty = new UUProperty();
        uuProperty.setUuid(uuidOwnerImportService.createUUID(certFingerprint, UUProperty.class.getSimpleName()).getUuid());
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

        uuProperty = uuPropertyDataSource.createAudit(uuProperty, certFingerprint);

        UUStatements uuPropertyStatement = new UUStatements();
        uuPropertyStatement.setSubject(uuObjectStatementUUID);
        uuPropertyStatement.setPredicate(UUStatementPredicate.HAS_PROPERTY);
        uuPropertyStatement.setObject(uuProperty.getUuid());

        uuStatementsDataSource.createAudit(uuPropertyStatement, certFingerprint);
        uuStatementsDataSource.createAudit(buildOpposite(uuPropertyStatement), certFingerprint);

        return uuProperty;
    }

    private UUPropertyValue saveUUPropertyValue(AggregateUUPropertyValue aggPropertyValue, String uuPropertyStatementUUID, String certFingerprint) {
        UUPropertyValue uuPropertyValue = new UUPropertyValue();
        uuPropertyValue.setUuid(uuidOwnerImportService.createUUID(certFingerprint, UUPropertyValue.class.getSimpleName()).getUuid());
        uuPropertyValue.setValue(aggPropertyValue.getValue());
        uuPropertyValue.setValueTypeCast(aggPropertyValue.getValueTypeCast());
        uuPropertyValue.setSourceType(aggPropertyValue.getSourceType());

        uuPropertyValue = uuPropertyValueDataSource.createAudit(uuPropertyValue, certFingerprint);

        UUStatements uuStatement = new UUStatements();
        uuStatement.setSubject(uuPropertyStatementUUID);
        uuStatement.setPredicate(UUStatementPredicate.HAS_VALUE);
        uuStatement.setObject(uuPropertyValue.getUuid());

        uuStatementsDataSource.createAudit(uuStatement, certFingerprint);
        uuStatementsDataSource.createAudit(buildOpposite(uuStatement), certFingerprint);

        return uuPropertyValue;
    }

    private UUStatements buildOpposite(UUStatements uuStatement) {
        UUStatements uuStatementOpposite = new UUStatements();
        uuStatementOpposite.setSubject(uuStatement.getObject());
        uuStatementOpposite.setPredicate(uuStatement.getPredicate().getOpposite(uuStatement.getPredicate()));
        uuStatementOpposite.setObject(uuStatement.getSubject());
        return uuStatementOpposite;
    }

}
