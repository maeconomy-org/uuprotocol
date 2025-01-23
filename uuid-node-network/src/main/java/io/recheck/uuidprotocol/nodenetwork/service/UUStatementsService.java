package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.common.security.service.ClientUUIDService;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUObjectDataSource;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUPropertyDataSource;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUStatementsDataSource;
import io.recheck.uuidprotocol.nodenetwork.dto.UUStatementsDTO;
import io.recheck.uuidprotocol.nodenetwork.model.UUStatementPredicate;
import io.recheck.uuidprotocol.nodenetwork.model.UUStatements;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UUStatementsService {

    private final ClientUUIDService clientUUIDService;

    private final UUStatementsDataSource uuStatementsDataSource;
    private final UUPropertyDataSource uuPropertyDataSource;
    private final UUObjectDataSource uuObjectDataSource;

    public UUStatements createOrUpdate(UUStatementsDTO uuStatementsDTO, String clientCertFingerprint) {
        validateCreateOrUpdate(uuStatementsDTO, clientCertFingerprint);
        return uuStatementsDataSource.createOrUpdateAudit(uuStatementsDTO.build(), clientCertFingerprint);
    }

    public UUStatements softDelete(String statementsId, String clientCertFingerprint) {
        validateSoftDelete(statementsId, clientCertFingerprint);
        return uuStatementsDataSource.softDeleteAudit(statementsId, clientCertFingerprint);
    }

    public void validateCreateOrUpdate(UUStatementsDTO uuStatementsDTO, String clientCertFingerprint) {
        clientUUIDService.validateClientUUID(clientCertFingerprint, uuStatementsDTO.getObject());
        clientUUIDService.validateClientUUID(clientCertFingerprint, uuStatementsDTO.getSubject());

        if (uuStatementsDTO.getPredicate().equals(UUStatementPredicate.IS_PROPERTY_OF)) {
            if (uuPropertyDataSource.findByDocumentId(uuStatementsDTO.getSubject()) == null) {
                throw new NotFoundException("Statement subject Property not found with uuid=" + uuStatementsDTO.getSubject());
            }
            if (uuObjectDataSource.findByDocumentId(uuStatementsDTO.getObject()) == null) {
                throw new NotFoundException("Statement object UUObject not found with uuid=" + uuStatementsDTO.getObject());
            }
        }
        else if (uuStatementsDTO.getPredicate().equals(UUStatementPredicate.HAS_PROPERTY)) {
            if (uuPropertyDataSource.findByDocumentId(uuStatementsDTO.getObject()) == null) {
                throw new NotFoundException("Statement object Property not found with uuid=" + uuStatementsDTO.getObject());
            }
            if (uuObjectDataSource.findByDocumentId(uuStatementsDTO.getSubject()) == null) {
                throw new NotFoundException("Statement subject UUObject not found with uuid=" + uuStatementsDTO.getSubject());
            }
        }
        else {
            if (uuObjectDataSource.findByDocumentId(uuStatementsDTO.getSubject()) == null) {
                throw new NotFoundException("Statement subject UUObject not found with uuid=" + uuStatementsDTO.getSubject());
            }
            if (uuObjectDataSource.findByDocumentId(uuStatementsDTO.getObject()) == null) {
                throw new NotFoundException("Statement object UUObject not found with uuid=" + uuStatementsDTO.getObject());
            }
        }
    }

    public void validateSoftDelete(String statementsId, String clientCertFingerprint) {
        UUStatements existingStatement = uuStatementsDataSource.findByDocumentId(statementsId);
        if (existingStatement == null) {
            throw new NotFoundException("Statement Id Not found for soft delete");
        }
        clientUUIDService.validateClientUUID(clientCertFingerprint, existingStatement.getObject());
        clientUUIDService.validateClientUUID(clientCertFingerprint, existingStatement.getSubject());
    }

}
