package io.recheck.uuidprotocol.nodenetwork.statements;

import io.recheck.uuidprotocol.common.exceptions.NodeTypeException;
import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.common.exceptions.PossibleStatementsException;
import io.recheck.uuidprotocol.domain.node.model.*;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRecord;
import io.recheck.uuidprotocol.domain.statements.dto.UUStatementDTO;
import io.recheck.uuidprotocol.domain.statements.model.UUStatementPredicate;
import io.recheck.uuidprotocol.domain.statements.model.UUStatements;
import io.recheck.uuidprotocol.domain.statements.model.UUStatementsClassType;
import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener.AggregateUUStatementsEventListener;
import io.recheck.uuidprotocol.nodenetwork.registrar.UUIDRegistrarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UUStatementsService {

    private final UUIDRegistrarService uuidRegistrarService;
    private final AggregateUUStatementsEventListener aggregateUUStatementsEventListener;
    private final UUStatementsDataSource uuStatementsDataSource;

    public List<UUStatements> findOrCreateWithOpposite(List<UUStatementDTO> uuStatementDTOList, UserDetailsCustom user) {
        Set<UUStatementDTO> uuStatementsSet = new HashSet<>(uuStatementDTOList);

        List<UUStatements> uuStatementsList = new ArrayList<>();
        for (UUStatementDTO uuStatementDTO : uuStatementsSet) {
            //exclude validation of owning uuids - everyone could make statements
            //validateOwnerUUID(uuStatementDTO, certFingerprint);
            UUStatements uuStatements = uuStatementDTO.build();
            validateStatement(uuStatementDTO.getSubject(), uuStatementDTO.getPredicate(), uuStatementDTO.getObject());
            uuStatementsList.add(findOrCreate(uuStatements, user));
            uuStatementsList.add(findOrCreate(uuStatements.buildOpposite(), user));
        }

        return uuStatementsList;
    }

    public List<UUStatements> softDeleteWithOpposite(UUStatementDTO uuStatementDTO, UserDetailsCustom user) {
        //exclude validation of owning uuids - everyone could soft delete statements
        //validateOwnerUUID(uuStatementDTO, certFingerprint);

        List<UUStatements> uuStatementsList = new ArrayList<>();

        UUStatements uuStatements = uuStatementDTO.build();
        uuStatementsList.add(softDelete(uuStatements, user));
        uuStatementsList.add(softDelete(uuStatements.buildOpposite(), user));

        return uuStatementsList;
    }

    private UUStatements findOrCreate(UUStatements uuStatements, UserDetailsCustom user) {
        UUStatements lastUpdated = uuStatementsDataSource.findLastUpdated(uuStatements);
        if (lastUpdated == null || lastUpdated.getSoftDeleted()) {
            lastUpdated = uuStatementsDataSource.createAudit(uuStatements, user);
            aggregateUUStatementsEventListener.postCreate(lastUpdated);
        }
        return lastUpdated;
    }

    private UUStatements softDelete(UUStatements uuStatements, UserDetailsCustom user) {
        UUStatements lastUpdated = uuStatementsDataSource.findLastUpdated(uuStatements);
        if (lastUpdated == null) {
            throw new NotFoundException("Not found for soft delete");
        }
        else if (!lastUpdated.getSoftDeleted()) {
            lastUpdated = uuStatementsDataSource.softDeleteAudit(lastUpdated, user);
        }
        return lastUpdated;
    }





    static private MultiValueMap<UUStatementPredicate, UUStatementsClassType> uuStatementValidationTypeMap;

    static {
        uuStatementValidationTypeMap = new LinkedMultiValueMap<>();

        uuStatementValidationTypeMap.add(UUStatementPredicate.IS_PARENT_OF, new UUStatementsClassType(UUObject.class, UUStatementPredicate.IS_PARENT_OF, UUObject.class));
        uuStatementValidationTypeMap.add(UUStatementPredicate.IS_CHILD_OF, new UUStatementsClassType(UUObject.class, UUStatementPredicate.IS_CHILD_OF, UUObject.class));

        uuStatementValidationTypeMap.add(UUStatementPredicate.IS_INPUT_OF, new UUStatementsClassType(UUObject.class, UUStatementPredicate.IS_INPUT_OF, UUObject.class));
        uuStatementValidationTypeMap.add(UUStatementPredicate.IS_OUTPUT_OF, new UUStatementsClassType(UUObject.class, UUStatementPredicate.IS_OUTPUT_OF, UUObject.class));

        uuStatementValidationTypeMap.add(UUStatementPredicate.IS_MODEL_OF, new UUStatementsClassType(UUObject.class, UUStatementPredicate.IS_MODEL_OF, UUObject.class));
        uuStatementValidationTypeMap.add(UUStatementPredicate.IS_INSTANCE_MODEL_OF, new UUStatementsClassType(UUObject.class, UUStatementPredicate.IS_INSTANCE_MODEL_OF, UUObject.class));

        uuStatementValidationTypeMap.add(UUStatementPredicate.IS_PROPERTY_OF, new UUStatementsClassType(UUProperty.class, UUStatementPredicate.IS_PROPERTY_OF, UUObject.class));
        uuStatementValidationTypeMap.add(UUStatementPredicate.HAS_PROPERTY, new UUStatementsClassType(UUObject.class, UUStatementPredicate.HAS_PROPERTY, UUProperty.class));

        uuStatementValidationTypeMap.add(UUStatementPredicate.IS_VALUE_OF, new UUStatementsClassType(UUPropertyValue.class, UUStatementPredicate.IS_VALUE_OF, UUProperty.class));
        uuStatementValidationTypeMap.add(UUStatementPredicate.HAS_VALUE, new UUStatementsClassType(UUProperty.class, UUStatementPredicate.HAS_VALUE, UUPropertyValue.class));

        uuStatementValidationTypeMap.add(UUStatementPredicate.IS_FILE_OF, new UUStatementsClassType(UUFile.class, UUStatementPredicate.IS_FILE_OF, UUPropertyValue.class));
        uuStatementValidationTypeMap.add(UUStatementPredicate.IS_FILE_OF, new UUStatementsClassType(UUFile.class, UUStatementPredicate.IS_FILE_OF, UUProperty.class));
        uuStatementValidationTypeMap.add(UUStatementPredicate.IS_FILE_OF, new UUStatementsClassType(UUFile.class, UUStatementPredicate.IS_FILE_OF, UUObject.class));
        uuStatementValidationTypeMap.add(UUStatementPredicate.HAS_FILE, new UUStatementsClassType(UUPropertyValue.class, UUStatementPredicate.HAS_FILE, UUFile.class));
        uuStatementValidationTypeMap.add(UUStatementPredicate.HAS_FILE, new UUStatementsClassType(UUProperty.class, UUStatementPredicate.HAS_FILE, UUFile.class));
        uuStatementValidationTypeMap.add(UUStatementPredicate.HAS_FILE, new UUStatementsClassType(UUObject.class, UUStatementPredicate.HAS_FILE, UUFile.class));

        uuStatementValidationTypeMap.add(UUStatementPredicate.HAS_ADDRESS, new UUStatementsClassType(UUObject.class, UUStatementPredicate.HAS_ADDRESS, UUAddress.class));
        uuStatementValidationTypeMap.add(UUStatementPredicate.IS_ADDRESS_OF, new UUStatementsClassType(UUAddress.class, UUStatementPredicate.IS_ADDRESS_OF, UUObject.class));
    }

    private void validateStatement(String subject, UUStatementPredicate predicate, String object) {
        if (subject.equals(object)) {
            throw new PossibleStatementsException("subject is equals to object");
        }

        UUIDRecord subjectUUID = uuidRegistrarService.findByUUID(subject);
        if (subjectUUID == null) {
            throw new NotFoundException("subject UUID=" + subject + " not found");
        }
        if (subjectUUID.getUuidRecordMeta().getNodeType() == null) {
            throw new NodeTypeException("subject type not defined");
        }

        UUIDRecord objectUUID = uuidRegistrarService.findByUUID(object);
        if (objectUUID == null) {
            throw new NotFoundException("object UUID=" + object + " not found");
        }
        if (objectUUID.getUuidRecordMeta().getNodeType() == null) {
            throw new NodeTypeException("object type not defined");
        }

        List<UUStatementsClassType> uuStatementsPossibleList = uuStatementValidationTypeMap.get(predicate);
        for (UUStatementsClassType uuStatementsPossible : uuStatementsPossibleList) {
            if (subjectUUID.getUuidRecordMeta().getNodeType().equals(uuStatementsPossible.getSubjectClass().getSimpleName()) &&
                    objectUUID.getUuidRecordMeta().getNodeType().equals(uuStatementsPossible.getObjectClass().getSimpleName())) {
                return;
            }
        }

        throw new PossibleStatementsException(
                "The possible statements for predicate: " + predicate + " are between: " + uuStatementsPossibleList + ".\n" +
                        "Your statement is : {" +
                        "subjectType=" + subjectUUID.getUuidRecordMeta().getNodeType() +
                        ", predicate=" + predicate +
                        ", objectType=" + objectUUID.getUuidRecordMeta().getNodeType() +
                        '}');
    }

    private void validateOwnerUUID(UUStatementDTO uuStatementDTO, String certFingerprint) {
        uuidRegistrarService.authorize(certFingerprint, uuStatementDTO.getObject());
        uuidRegistrarService.authorize(certFingerprint, uuStatementDTO.getSubject());
    }

}