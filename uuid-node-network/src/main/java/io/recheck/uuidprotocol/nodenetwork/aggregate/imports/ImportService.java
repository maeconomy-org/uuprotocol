package io.recheck.uuidprotocol.nodenetwork.aggregate.imports;

import io.recheck.uuidprotocol.domain.aggregate.model.*;
import io.recheck.uuidprotocol.domain.node.model.*;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.AggregateRepository;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.UUFileDataSource;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.UUObjectDataSource;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.UUPropertyDataSource;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.UUPropertyValueDataSource;
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
    private final UUStatementsDataSource uuStatementsDataSource;

    private final AggregateRepository aggregateRepository;

    private final UUIDOwnerImportService uuidOwnerImportService;




    public void saveAll(List<AggregateEntity> aggregateEntityList, String certFingerprint) {
        //updateNodeType!!!!!
        for (AggregateEntity aggObject : aggregateEntityList) {
            UUObject uuObject = saveUUObject(aggObject, certFingerprint);
            setAggNode(aggObject, uuObject);

            if (aggObject.getFiles() != null) {
                for (AggregateFile aggFile : aggObject.getFiles()) {
                    UUFile uuFile = saveUUFile(aggFile, uuObject.getUuid(), certFingerprint);
                    setAggNode(aggFile, uuFile);
                }
            }

            if (aggObject.getProperties() != null) {
                for (AggregateProperty aggProperty : aggObject.getProperties()) {
                    UUProperty uuProperty = saveUUProperty(aggProperty, uuObject.getUuid(), certFingerprint);
                    setAggNode(aggProperty, uuProperty);

                    if (aggProperty.getFiles() != null) {
                        for (AggregateFile aggPropertyFile : aggProperty.getFiles()) {
                            UUFile uuFile = saveUUFile(aggPropertyFile, uuProperty.getUuid(), certFingerprint);
                            setAggNode(aggPropertyFile, uuFile);
                        }
                    }

                    if (aggProperty.getValues() != null) {
                        for (AggregatePropertyValue aggPropertyValue : aggProperty.getValues()) {
                            UUPropertyValue uuPropertyValue = saveUUPropertyValue(aggPropertyValue, uuProperty.getUuid(), certFingerprint);
                            setAggNode(aggPropertyValue, uuPropertyValue);

                            if (aggPropertyValue.getFiles() != null) {
                                for (AggregateFile aggPropertyValueFile : aggPropertyValue.getFiles()) {
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

    private void setAggNode(AggregatedNode aggNode, Node uuNode) {
        aggNode.setUuid(uuNode.getUuid());
        aggNode.setCreatedAt(uuNode.getCreatedAt());
        aggNode.setCreatedBy(uuNode.getCreatedBy());
        aggNode.setLastUpdatedAt(uuNode.getLastUpdatedAt());
        aggNode.setLastUpdatedBy(uuNode.getLastUpdatedBy());
    }

    private UUObject saveUUObject(AggregateEntity aggObject, String certFingerprint) {
        UUObject uuObject = new UUObject();
        uuObject.setUuid(uuidOwnerImportService.createUUID(certFingerprint, uuObjectDataSource.getCollectionType().getSimpleName()).getUuid());
        uuObject.setName(aggObject.getName());
        uuObject.setAbbreviation(aggObject.getAbbreviation());
        uuObject.setVersion(aggObject.getVersion());
        uuObject.setDescription(aggObject.getDescription());

        return uuObjectDataSource.createAudit(uuObject, certFingerprint);
    }

    private UUFile saveUUFile(AggregateFile aggFile, String nodeStatementUUID, String certFingerprint) {
        UUFile uuFile = new UUFile();
        uuFile.setUuid(uuidOwnerImportService.createUUID(certFingerprint, uuFileDataSource.getCollectionType().getSimpleName()).getUuid());
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

    private UUProperty saveUUProperty(AggregateProperty aggProperty, String uuObjectStatementUUID, String certFingerprint) {
        UUProperty uuProperty = new UUProperty();
        uuProperty.setUuid(uuidOwnerImportService.createUUID(certFingerprint, uuPropertyDataSource.getCollectionType().getSimpleName()).getUuid());
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

    private UUPropertyValue saveUUPropertyValue(AggregatePropertyValue aggPropertyValue, String uuPropertyStatementUUID, String certFingerprint) {
        UUPropertyValue uuPropertyValue = new UUPropertyValue();
        uuPropertyValue.setUuid(uuidOwnerImportService.createUUID(certFingerprint, uuPropertyValueDataSource.getCollectionType().getSimpleName()).getUuid());
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
