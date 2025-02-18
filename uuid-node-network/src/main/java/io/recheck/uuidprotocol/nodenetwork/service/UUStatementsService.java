package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.domain.node.datasource.UUStatementsDataSource;
import io.recheck.uuidprotocol.domain.node.dto.UUStatementDTO;
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
    private final UUStatementPredicateService uuStatementPredicateService;


    public UUStatementDTO buildOpposite(UUStatementDTO uuStatementDTO) {
        return new UUStatementDTO(uuStatementDTO.getObject(), uuStatementDTO.getPredicate().getOpposite(uuStatementDTO.getPredicate()), uuStatementDTO.getSubject());
    }

    public Set<UUStatements> findOrCreate(List<UUStatementDTO> uuStatementDTOList, String ownerCertFingerprint) {
        Set<UUStatementDTO> uuStatementsSet = new HashSet<>(uuStatementDTOList);

        List<UUStatements> uuStatementsList = new ArrayList<>();
        for (UUStatementDTO uuStatementDTO : uuStatementsSet) {
            //exclude validation of owning uuids - everyone could make statements
//            validateOwnerUUID(uuStatementDTO, ownerCertFingerprint);
            uuStatementPredicateService.validateStatement(uuStatementDTO.getSubject(), uuStatementDTO.getPredicate(), uuStatementDTO.getObject());
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
        //exclude validation of owning uuids - everyone could soft delete statements
//        validateOwnerUUID(uuStatementDTO, ownerCertFingerprint);
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

    public void validateOwnerUUID(UUStatementDTO uuStatementDTO, String ownerCertFingerprint) {
        ownerUUIDService.validateOwnerUUID(ownerCertFingerprint, uuStatementDTO.getObject());
        ownerUUIDService.validateOwnerUUID(ownerCertFingerprint, uuStatementDTO.getSubject());
    }

}