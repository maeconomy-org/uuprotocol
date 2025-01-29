package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.domain.node.datasource.UUObjectDataSource;
import io.recheck.uuidprotocol.domain.node.datasource.UUPropertyDataSource;
import io.recheck.uuidprotocol.domain.node.datasource.UUStatementsDataSource;
import io.recheck.uuidprotocol.domain.node.dto.UUStatementDTO;
import io.recheck.uuidprotocol.domain.node.model.UUStatementPredicate;
import io.recheck.uuidprotocol.domain.node.model.UUStatements;
import io.recheck.uuidprotocol.domain.uuidowner.OwnerUUIDService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UUStatementsService {

    private final OwnerUUIDService ownerUUIDService;

    private final UUStatementsDataSource uuStatementsDataSource;
    private final UUPropertyDataSource uuPropertyDataSource;
    private final UUObjectDataSource uuObjectDataSource;



    public UUStatementDTO buildOpposite(UUStatementDTO uuStatementDTO) {
        return new UUStatementDTO(uuStatementDTO.getObject(), uuStatementDTO.getPredicate().getOpposite(uuStatementDTO.getPredicate()), uuStatementDTO.getSubject());
    }

    public HashSet<UUStatements> findOrCreate(List<UUStatementDTO> uuStatementDTOList, String ownerCertFingerprint) {
        Set<UUStatementDTO> uuStatementsSet = new HashSet<>(uuStatementDTOList);

        List<UUStatements> uuStatementsList = new ArrayList<>();
        for (UUStatementDTO uuStatementDTO : uuStatementsSet) {
            validateFindOrCreate(uuStatementDTO, ownerCertFingerprint);
            uuStatementsList.add(findOrCreate(uuStatementDTO, ownerCertFingerprint));
            uuStatementsList.add(findOrCreate(buildOpposite(uuStatementDTO), ownerCertFingerprint));
        }
        return new HashSet<>(uuStatementsList);
    }

    public UUStatements findOrCreate(UUStatementDTO uuStatementDTO, String ownerCertFingerprint) {
        UUStatements existingUUStatement = find(uuStatementDTO);
        if (existingUUStatement == null) {
            return uuStatementsDataSource.createOrUpdateAudit(uuStatementDTO.build(), ownerCertFingerprint);
        }
        return existingUUStatement;
    }

    public List<UUStatements> softDelete(UUStatementDTO uuStatementDTO, String ownerCertFingerprint) {
        validateownerUUID(uuStatementDTO, ownerCertFingerprint);
        UUStatements existingUUStatement = find(uuStatementDTO);
        UUStatements existingOppositeUUStatement = find(buildOpposite(uuStatementDTO));

        List<UUStatements> uuStatementsList = new ArrayList<>();
        if (existingUUStatement != null) {
            uuStatementsList.add(uuStatementsDataSource.softDeleteAudit(existingUUStatement, ownerCertFingerprint));
        }
        if (existingOppositeUUStatement != null) {
            uuStatementsList.add(uuStatementsDataSource.softDeleteAudit(existingOppositeUUStatement, ownerCertFingerprint));
        }

        return uuStatementsList;
    }

    public UUStatements find(UUStatementDTO uuStatementDTO) {
        return uuStatementsDataSource.find(uuStatementDTO.getSubject(), uuStatementDTO.getPredicate().name(), uuStatementDTO.getObject());
    }

    public void validateFindOrCreate(UUStatementDTO uuStatementDTO, String ownerCertFingerprint) {
        validateownerUUID(uuStatementDTO, ownerCertFingerprint);

        //if predicate is IS_PROPERTY_OF or HAS_PROPERTY the allowed relations are between only UUObject with UUProperty
        //for that there is validation
        if (uuStatementDTO.getPredicate().equals(UUStatementPredicate.IS_PROPERTY_OF)) {
            if (uuPropertyDataSource.findByUUIDAndSoftDeletedFalse(uuStatementDTO.getSubject()) == null) {
                throw new NotFoundException("Statement subject Property not found with uuid=" + uuStatementDTO.getSubject());
            }
            if (uuObjectDataSource.findByUUIDAndSoftDeletedFalse(uuStatementDTO.getObject()) == null) {
                throw new NotFoundException("Statement object UUObject not found with uuid=" + uuStatementDTO.getObject());
            }
        }
        else if (uuStatementDTO.getPredicate().equals(UUStatementPredicate.HAS_PROPERTY)) {
            if (uuPropertyDataSource.findByUUIDAndSoftDeletedFalse(uuStatementDTO.getObject()) == null) {
                throw new NotFoundException("Statement object Property not found with uuid=" + uuStatementDTO.getObject());
            }
            if (uuObjectDataSource.findByUUIDAndSoftDeletedFalse(uuStatementDTO.getSubject()) == null) {
                throw new NotFoundException("Statement subject UUObject not found with uuid=" + uuStatementDTO.getSubject());
            }
        }
        else {
            // else both must be UUObject
            if (uuObjectDataSource.findByUUIDAndSoftDeletedFalse(uuStatementDTO.getSubject()) == null) {
                throw new NotFoundException("Statement subject UUObject not found with uuid=" + uuStatementDTO.getSubject());
            }
            if (uuObjectDataSource.findByUUIDAndSoftDeletedFalse(uuStatementDTO.getObject()) == null) {
                throw new NotFoundException("Statement object UUObject not found with uuid=" + uuStatementDTO.getObject());
            }
        }
    }

    public void validateownerUUID(UUStatementDTO uuStatementDTO, String ownerCertFingerprint) {
        ownerUUIDService.validateOwnerUUID(ownerCertFingerprint, uuStatementDTO.getObject());
        ownerUUIDService.validateOwnerUUID(ownerCertFingerprint, uuStatementDTO.getSubject());
    }

}
