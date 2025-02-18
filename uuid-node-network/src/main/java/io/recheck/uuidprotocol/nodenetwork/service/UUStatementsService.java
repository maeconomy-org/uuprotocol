package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.common.security.service.ClientUUIDService;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUObjectDataSource;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUPropertyDataSource;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUStatementsDataSource;
import io.recheck.uuidprotocol.nodenetwork.dto.UUStatementDTO;
import io.recheck.uuidprotocol.nodenetwork.model.UUStatementPredicate;
import io.recheck.uuidprotocol.nodenetwork.model.UUStatements;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UUStatementsService {

    private final ClientUUIDService clientUUIDService;

    private final UUStatementsDataSource uuStatementsDataSource;
    private final UUPropertyDataSource uuPropertyDataSource;
    private final UUObjectDataSource uuObjectDataSource;

    Map<UUStatementPredicate, UUStatementPredicate> oppositePredicateOf = Map.of(
            UUStatementPredicate.IS_PARENT_OF, UUStatementPredicate.IS_CHILD_OF,
            UUStatementPredicate.IS_CHILD_OF, UUStatementPredicate.IS_PARENT_OF,

            UUStatementPredicate.IS_INPUT_OF, UUStatementPredicate.IS_OUTPUT_OF,
            UUStatementPredicate.IS_OUTPUT_OF, UUStatementPredicate.IS_INPUT_OF,

            UUStatementPredicate.IS_MODEL_OF, UUStatementPredicate.IS_INSTANCE_MODEL_OF,
            UUStatementPredicate.IS_INSTANCE_MODEL_OF, UUStatementPredicate.IS_MODEL_OF,

            UUStatementPredicate.IS_PROPERTY_OF, UUStatementPredicate.HAS_PROPERTY,
            UUStatementPredicate.HAS_PROPERTY, UUStatementPredicate.IS_PROPERTY_OF
    );

    public UUStatementDTO buildOpposite(UUStatementDTO uuStatementDTO) {
        return new UUStatementDTO(uuStatementDTO.getObject(), oppositePredicateOf.get(uuStatementDTO.getPredicate()), uuStatementDTO.getSubject());
    }

    public HashSet<UUStatements> create(List<UUStatementDTO> uuStatementDTOList, String clientCertFingerprint) {
        Set<UUStatementDTO> uuStatementsSet = new HashSet<>(uuStatementDTOList);

        List<UUStatements> uuStatementsList = new ArrayList<>();
        for (UUStatementDTO uuStatementDTO : uuStatementsSet) {
            validateFindOrCreate(uuStatementDTO, clientCertFingerprint);
            uuStatementsList.add(findOrCreate(uuStatementDTO, clientCertFingerprint));
            uuStatementsList.add(findOrCreate(buildOpposite(uuStatementDTO), clientCertFingerprint));
        }
        return new HashSet<>(uuStatementsList);
    }

    public UUStatements findOrCreate(UUStatementDTO uuStatementDTO, String clientCertFingerprint) {
        UUStatements existingUUStatement = find(uuStatementDTO);
        if (existingUUStatement == null) {
            return uuStatementsDataSource.createOrUpdateAudit(uuStatementDTO.build(), clientCertFingerprint);
        }
        return existingUUStatement;
    }

    public UUStatements find(UUStatementDTO uuStatementDTO) {
        return uuStatementsDataSource.find(uuStatementDTO.getSubject(), uuStatementDTO.getPredicate().name(), uuStatementDTO.getObject());
    }

    public List<UUStatements> softDelete(UUStatementDTO uuStatementDTO, String clientCertFingerprint) {
        validateClientUUID(uuStatementDTO, clientCertFingerprint);
        UUStatements existingUUStatement = find(uuStatementDTO);
        UUStatements existingOppositeUUStatement = find(buildOpposite(uuStatementDTO));

        List<UUStatements> uuStatementsList = new ArrayList<>();
        if (existingUUStatement != null) {
            uuStatementsList.add(uuStatementsDataSource.softDeleteAudit(existingUUStatement.getId(), clientCertFingerprint));
        }
        if (existingOppositeUUStatement != null) {
            uuStatementsList.add(uuStatementsDataSource.softDeleteAudit(existingOppositeUUStatement.getId(), clientCertFingerprint));
        }

        return uuStatementsList;
    }

    public void validateFindOrCreate(UUStatementDTO uuStatementDTO, String clientCertFingerprint) {
        validateClientUUID(uuStatementDTO, clientCertFingerprint);

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

    public void validateClientUUID(UUStatementDTO uuStatementDTO, String clientCertFingerprint) {
        clientUUIDService.validateClientUUID(clientCertFingerprint, uuStatementDTO.getObject());
        clientUUIDService.validateClientUUID(clientCertFingerprint, uuStatementDTO.getSubject());
    }

}
