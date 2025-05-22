package io.recheck.uuidprotocol.nodenetwork.statements;

import io.recheck.uuidprotocol.common.exceptions.NodeTypeException;
import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.common.exceptions.PossibleStatementsException;
import io.recheck.uuidprotocol.domain.node.dto.UUStatementDTO;
import io.recheck.uuidprotocol.domain.node.model.UUStatementPredicate;
import io.recheck.uuidprotocol.domain.node.model.UUStatements;
import io.recheck.uuidprotocol.domain.owner.model.UUIDOwner;
import io.recheck.uuidprotocol.nodenetwork.aggregate.AggregateService;
import io.recheck.uuidprotocol.nodenetwork.owner.UUIDOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UUStatementsService {

    private final UUIDOwnerService uuidOwnerService;
    private final UUStatementsDataSource uuStatementsDataSource;
    private final AggregateService aggregateService;

    public List<UUStatements> findOrCreate(List<UUStatementDTO> uuStatementDTOList, String certFingerprint) {
        Set<UUStatementDTO> uuStatementsSet = new HashSet<>(uuStatementDTOList);

        List<UUStatements> uuStatementsList = new ArrayList<>();
        for (UUStatementDTO uuStatementDTO : uuStatementsSet) {
            //exclude validation of owning uuids - everyone could make statements
//            validateOwnerUUID(uuStatementDTO, certFingerprint);
            validateStatement(uuStatementDTO.getSubject(), uuStatementDTO.getPredicate(), uuStatementDTO.getObject());
            uuStatementsList.add(findOrCreate(uuStatementDTO, certFingerprint));
            uuStatementsList.add(findOrCreate(buildOpposite(uuStatementDTO), certFingerprint));
        }

        return uuStatementsList;
    }

    public List<UUStatements> softDelete(UUStatementDTO uuStatementDTO, String certFingerprint) {
        //exclude validation of owning uuids - everyone could soft delete statements
//        validateOwnerUUID(uuStatementDTO, certFingerprint);

        List<UUStatements> uuStatementsList = new ArrayList<>();
        uuStatementsList.add(softDelete_(uuStatementDTO, certFingerprint));
        uuStatementsList.add(softDelete_(buildOpposite(uuStatementDTO), certFingerprint));

        return uuStatementsList;
    }

    private UUStatementDTO buildOpposite(UUStatementDTO uuStatementDTO) {
        return new UUStatementDTO(uuStatementDTO.getObject(), uuStatementDTO.getPredicate().getOpposite(uuStatementDTO.getPredicate()), uuStatementDTO.getSubject());
    }

    private UUStatements findOrCreate(UUStatementDTO uuStatementDTO, String certFingerprint) {
        UUStatements existingUUStatement = find(uuStatementDTO);
        if (existingUUStatement == null) {
            existingUUStatement = uuStatementsDataSource.createOrUpdateAudit(uuStatementDTO.build(), certFingerprint);
            aggregateService.createStatement(existingUUStatement);
        }
        return existingUUStatement;
    }

    private UUStatements softDelete_(UUStatementDTO uuStatementDTO, String certFingerprint) {
        UUStatements existingUUStatement = find(uuStatementDTO);
        if (existingUUStatement != null) {
            existingUUStatement = uuStatementsDataSource.softDeleteAudit(existingUUStatement, certFingerprint);
            aggregateService.deleteStatement(existingUUStatement);
        }
        return existingUUStatement;
    }

    private UUStatements find(UUStatementDTO uuStatementDTO) {
        return uuStatementsDataSource.find(uuStatementDTO.getSubject(), uuStatementDTO.getPredicate().name(), uuStatementDTO.getObject());
    }

    private void validateStatement(String subject, UUStatementPredicate predicate, String object) {
        if (subject.equals(object)) {
            throw new PossibleStatementsException("subject is equals to object");
        }

        UUIDOwner subjectUUID = uuidOwnerService.findByUUID(subject);
        if (subjectUUID == null) {
            throw new NotFoundException("subject UUID=" + subject + " not found");
        }
        if (subjectUUID.getNodeType() == null) {
            throw new NodeTypeException("subject type not defined");
        }

        UUIDOwner objectUUID = uuidOwnerService.findByUUID(object);
        if (objectUUID == null) {
            throw new NotFoundException("object UUID=" + object + " not found");
        }
        if (objectUUID.getNodeType() == null) {
            throw new NodeTypeException("object type not defined");
        }

        List<UUStatementsClass> uuStatementsPossibleList1 = UUStatementsClassPredicateMap.get(predicate);
        for (UUStatementsClass uuStatementsPossible : uuStatementsPossibleList1) {
            if (subjectUUID.getNodeType().equals(uuStatementsPossible.getSubjectClass().getSimpleName()) &&
                    objectUUID.getNodeType().equals(uuStatementsPossible.getObjectClass().getSimpleName())) {
                return;
            }
        }

        throw new PossibleStatementsException(
                "The possible statements for predicate: " + predicate + " are between: " + uuStatementsPossibleList1 + ".\n" +
                        "Your statement is : {" +
                        "subjectType=" + subjectUUID.getNodeType() +
                        ", predicate=" + predicate +
                        ", objectType=" + objectUUID.getNodeType() +
                        '}');
    }

    private void validateOwnerUUID(UUStatementDTO uuStatementDTO, String certFingerprint) {
        uuidOwnerService.validateOwnerUUID(certFingerprint, uuStatementDTO.getObject());
        uuidOwnerService.validateOwnerUUID(certFingerprint, uuStatementDTO.getSubject());
    }

}