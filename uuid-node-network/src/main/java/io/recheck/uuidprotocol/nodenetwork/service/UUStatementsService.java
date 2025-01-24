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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UUStatementsService {

    private final ClientUUIDService clientUUIDService;

    private final UUStatementsDataSource uuStatementsDataSource;
    private final UUPropertyDataSource uuPropertyDataSource;
    private final UUObjectDataSource uuObjectDataSource;

    public List<UUStatements> create(List<UUStatementsDTO> uuStatementsDTOList, String clientCertFingerprint) {
        List<UUStatements> uuStatementsList = new ArrayList<>();
        for (UUStatementsDTO uuStatementsDTO : uuStatementsDTOList) {
            uuStatementsList.add(findOrCreate(uuStatementsDTO, clientCertFingerprint));
        }
        return uuStatementsList;
    }

    public UUStatements findOrCreate(UUStatementsDTO uuStatementsDTO, String clientCertFingerprint) {
        validateCreateOrUpdate(uuStatementsDTO, clientCertFingerprint);
        UUStatements existingUUStatement = uuStatementsDataSource.find(uuStatementsDTO.getSubject(), uuStatementsDTO.getPredicate().name(), uuStatementsDTO.getObject());
        if (existingUUStatement == null) {
            return uuStatementsDataSource.createOrUpdateAudit(uuStatementsDTO.build(), clientCertFingerprint);
        }
        return existingUUStatement;
    }

    public UUStatements softDelete(String statementsId, String clientCertFingerprint) {
        validateSoftDelete(statementsId, clientCertFingerprint);
        return uuStatementsDataSource.softDeleteAudit(statementsId, clientCertFingerprint);
    }

    public void validateCreateOrUpdate(UUStatementsDTO uuStatementsDTO, String clientCertFingerprint) {
        clientUUIDService.validateClientUUID(clientCertFingerprint, uuStatementsDTO.getObject());
        clientUUIDService.validateClientUUID(clientCertFingerprint, uuStatementsDTO.getSubject());

        //if predicate is IS_PROPERTY_OF or HAS_PROPERTY the allowed relations are between only UUObject with UUProperty
        //for that there is validation
        if (uuStatementsDTO.getPredicate().equals(UUStatementPredicate.IS_PROPERTY_OF)) {
            if (uuPropertyDataSource.findByUUIDAndSoftDeletedFalse(uuStatementsDTO.getSubject()) == null) {
                throw new NotFoundException("Statement subject Property not found with uuid=" + uuStatementsDTO.getSubject());
            }
            if (uuObjectDataSource.findByUUIDAndSoftDeletedFalse(uuStatementsDTO.getObject()) == null) {
                throw new NotFoundException("Statement object UUObject not found with uuid=" + uuStatementsDTO.getObject());
            }
        }
        else if (uuStatementsDTO.getPredicate().equals(UUStatementPredicate.HAS_PROPERTY)) {
            if (uuPropertyDataSource.findByUUIDAndSoftDeletedFalse(uuStatementsDTO.getObject()) == null) {
                throw new NotFoundException("Statement object Property not found with uuid=" + uuStatementsDTO.getObject());
            }
            if (uuObjectDataSource.findByUUIDAndSoftDeletedFalse(uuStatementsDTO.getSubject()) == null) {
                throw new NotFoundException("Statement subject UUObject not found with uuid=" + uuStatementsDTO.getSubject());
            }
        }
        else {
            // else both must be UUObject
            if (uuObjectDataSource.findByUUIDAndSoftDeletedFalse(uuStatementsDTO.getSubject()) == null) {
                throw new NotFoundException("Statement subject UUObject not found with uuid=" + uuStatementsDTO.getSubject());
            }
            if (uuObjectDataSource.findByUUIDAndSoftDeletedFalse(uuStatementsDTO.getObject()) == null) {
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
