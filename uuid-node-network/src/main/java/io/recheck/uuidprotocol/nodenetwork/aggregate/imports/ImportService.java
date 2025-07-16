package io.recheck.uuidprotocol.nodenetwork.aggregate.imports;

import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.domain.node.model.UUStatementPredicate;
import io.recheck.uuidprotocol.domain.node.model.UUStatements;
import io.recheck.uuidprotocol.nodenetwork.aggregate.model.AggregateEntity;
import io.recheck.uuidprotocol.nodenetwork.aggregate.model.AggregateFile;
import io.recheck.uuidprotocol.nodenetwork.aggregate.model.AggregateProperty;
import io.recheck.uuidprotocol.nodenetwork.aggregate.model.AggregatePropertyValue;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.repository.AggregateRepositoryTemplate;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUFileDataSource;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUObjectDataSource;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUPropertyDataSource;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUPropertyValueDataSource;
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

    private final AggregateRepositoryTemplate aggregateRepositoryTemplate;

    private final UUIDOwnerImportService uuidOwnerImportService;




    public void saveAll(List<AggregateEntity> aggregateEntityList, String certFingerprint) {
        for (AggregateEntity aggregateEntity : aggregateEntityList) {
            aggregateEntity.setUuid(uuidOwnerImportService.createUUID(certFingerprint).getUuid());
            uuObjectDataSource.createOrUpdateAudit(aggregateEntity, certFingerprint);

            if (aggregateEntity.getFiles() != null) {
                for (AggregateFile uuFile : aggregateEntity.getFiles()) {
                    saveUUFile(uuFile, aggregateEntity, certFingerprint);
                }
            }

            if (aggregateEntity.getProperties() != null) {
                for (AggregateProperty uuProperty : aggregateEntity.getProperties()) {
                    saveUUProperty(uuProperty, aggregateEntity, certFingerprint);

                    if (uuProperty.getFiles() != null) {
                        for (AggregateFile uuPropertyFile : uuProperty.getFiles()) {
                            saveUUFile(uuPropertyFile, uuProperty, certFingerprint);
                        }
                    }

                    if (uuProperty.getValues() != null) {
                        for (AggregatePropertyValue uuPropertyValue : uuProperty.getValues()) {
                            saveUUPropertyValue(uuPropertyValue, uuProperty, certFingerprint);

                            if (uuPropertyValue.getFiles() != null) {
                                for (AggregateFile uuPropertyValueFile : uuPropertyValue.getFiles()) {
                                    saveUUFile(uuPropertyValueFile, uuPropertyValue, certFingerprint);
                                }
                            }
                        }
                    }

                }
            }
        }

        aggregateRepositoryTemplate.saveAll(aggregateEntityList);

    }


    private void saveUUFile(AggregateFile uuFile, Node nodeStatement, String certFingerprint) {
        uuFile.setUuid(uuidOwnerImportService.createUUID(certFingerprint).getUuid());
        uuFileDataSource.createOrUpdateAudit(uuFile, certFingerprint);

        UUStatements uuStatement = new UUStatements();
        uuStatement.setSubject(nodeStatement.getUuid());
        uuStatement.setPredicate(UUStatementPredicate.HAS_FILE);
        uuStatement.setObject(uuFile.getUuid());

        uuStatementsDataSource.createOrUpdateAudit(uuStatement, certFingerprint);
        uuStatementsDataSource.createOrUpdateAudit(buildOpposite(uuStatement), certFingerprint);
    }

    private void saveUUProperty(AggregateProperty uuProperty, AggregateEntity aggregateEntity, String certFingerprint) {
        uuProperty.setUuid(uuidOwnerImportService.createUUID(certFingerprint).getUuid());
        uuPropertyDataSource.createOrUpdateAudit(uuProperty, certFingerprint);

        UUStatements uuPropertyStatement = new UUStatements();
        uuPropertyStatement.setSubject(aggregateEntity.getUuid());
        uuPropertyStatement.setPredicate(UUStatementPredicate.HAS_PROPERTY);
        uuPropertyStatement.setObject(uuProperty.getUuid());

        uuStatementsDataSource.createOrUpdateAudit(uuPropertyStatement, certFingerprint);
        uuStatementsDataSource.createOrUpdateAudit(buildOpposite(uuPropertyStatement), certFingerprint);
    }

    private void saveUUPropertyValue(AggregatePropertyValue uuPropertyValue, AggregateProperty uuProperty, String certFingerprint) {
        uuPropertyValue.setUuid(uuidOwnerImportService.createUUID(certFingerprint).getUuid());
        uuPropertyValueDataSource.createOrUpdateAudit(uuPropertyValue, certFingerprint);

        UUStatements uuStatement = new UUStatements();
        uuStatement.setSubject(uuProperty.getUuid());
        uuStatement.setPredicate(UUStatementPredicate.HAS_VALUE);
        uuStatement.setObject(uuPropertyValue.getUuid());

        uuStatementsDataSource.createOrUpdateAudit(uuStatement, certFingerprint);
        uuStatementsDataSource.createOrUpdateAudit(buildOpposite(uuStatement), certFingerprint);
    }

    private UUStatements buildOpposite(UUStatements uuStatement) {
        UUStatements uuStatementOpposite = new UUStatements();
        uuStatementOpposite.setSubject(uuStatement.getObject());
        uuStatementOpposite.setPredicate(uuStatement.getPredicate().getOpposite(uuStatement.getPredicate()));
        uuStatementOpposite.setObject(uuStatement.getSubject());
        return uuStatementOpposite;
    }

}
