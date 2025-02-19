package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.common.exceptions.NodeTypeException;
import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.common.exceptions.PossibleStatementsException;
import io.recheck.uuidprotocol.domain.node.model.*;
import io.recheck.uuidprotocol.domain.uuidowner.UUIDOwner;
import io.recheck.uuidprotocol.domain.uuidowner.UUIDOwnerService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

@Service
public class UUStatementsPossibleMapService {

    @Data
    @AllArgsConstructor
    private class UUStatementsPossible {
        private final String subjectType;
        private final UUStatementPredicate predicate;
        private final String objectType;

        public UUStatementsPossible(Class subjectClassType, UUStatementPredicate predicate, Class objectClassType) {
            this.subjectType = subjectClassType.getSimpleName();
            this.predicate = predicate;
            this.objectType = objectClassType.getSimpleName();
        }

        @Override
        public String toString() {
            return "{" +
                    "subjectType=" + subjectType +
                    ", predicate=" + predicate +
                    ", objectType=" + objectType +
                    '}';
        }
    }

    private final MultiValueMap<UUStatementPredicate, UUStatementsPossible> uuStatementsPossibleMap;
    private final UUIDOwnerService uuidOwnerService;

    public UUStatementsPossibleMapService(UUIDOwnerService uuidOwnerService) {
        this.uuidOwnerService = uuidOwnerService;

        List<UUStatementsPossible> uuStatementsPossibleList = new ArrayList<>();
        uuStatementsPossibleList.add(new UUStatementsPossible(UUObject.class, UUStatementPredicate.IS_PARENT_OF, UUObject.class));
        uuStatementsPossibleList.add(new UUStatementsPossible(UUObject.class, UUStatementPredicate.IS_CHILD_OF, UUObject.class));

        uuStatementsPossibleList.add(new UUStatementsPossible(UUObject.class, UUStatementPredicate.IS_INPUT_OF, UUObject.class));
        uuStatementsPossibleList.add(new UUStatementsPossible(UUObject.class, UUStatementPredicate.IS_OUTPUT_OF, UUObject.class));

        uuStatementsPossibleList.add(new UUStatementsPossible(UUObject.class, UUStatementPredicate.IS_MODEL_OF, UUObject.class));
        uuStatementsPossibleList.add(new UUStatementsPossible(UUObject.class, UUStatementPredicate.IS_INSTANCE_MODEL_OF, UUObject.class));

        uuStatementsPossibleList.add(new UUStatementsPossible(UUProperty.class, UUStatementPredicate.IS_PROPERTY_OF, UUObject.class));
        uuStatementsPossibleList.add(new UUStatementsPossible(UUObject.class, UUStatementPredicate.HAS_PROPERTY, UUProperty.class));

        uuStatementsPossibleList.add(new UUStatementsPossible(UUPropertyValue.class, UUStatementPredicate.IS_VALUE_OF, UUProperty.class));
        uuStatementsPossibleList.add(new UUStatementsPossible(UUProperty.class, UUStatementPredicate.HAS_VALUE, UUPropertyValue.class));

        uuStatementsPossibleList.add(new UUStatementsPossible(UUFile.class, UUStatementPredicate.IS_FILE_OF, UUPropertyValue.class));
        uuStatementsPossibleList.add(new UUStatementsPossible(UUFile.class, UUStatementPredicate.IS_FILE_OF, UUProperty.class));
        uuStatementsPossibleList.add(new UUStatementsPossible(UUFile.class, UUStatementPredicate.IS_FILE_OF, UUObject.class));
        uuStatementsPossibleList.add(new UUStatementsPossible(UUPropertyValue.class, UUStatementPredicate.HAS_FILE, UUFile.class));
        uuStatementsPossibleList.add(new UUStatementsPossible(UUProperty.class, UUStatementPredicate.HAS_FILE, UUFile.class));
        uuStatementsPossibleList.add(new UUStatementsPossible(UUObject.class, UUStatementPredicate.HAS_FILE, UUFile.class));

        uuStatementsPossibleMap = new LinkedMultiValueMap<>();
        for (UUStatementsPossible uuStatementsPossible : uuStatementsPossibleList) {
            List<UUStatementsPossible> uuStatementsPossibleList1 = uuStatementsPossibleMap.get(uuStatementsPossible.getPredicate());
            if (uuStatementsPossibleList1 == null) {
                uuStatementsPossibleList1 = new ArrayList<>();
            }
            uuStatementsPossibleList1.add(uuStatementsPossible);
            uuStatementsPossibleMap.put(uuStatementsPossible.getPredicate(), uuStatementsPossibleList1);
        }
    }

    public void validateStatement(String subject, UUStatementPredicate predicate, String object) {
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

        List<UUStatementsPossible> uuStatementsPossibleList1 = uuStatementsPossibleMap.get(predicate);
        for (UUStatementsPossible uuStatementsPossible : uuStatementsPossibleList1) {
            if (subjectUUID.getNodeType().equals(uuStatementsPossible.getSubjectType()) &&
                    objectUUID.getNodeType().equals(uuStatementsPossible.getObjectType())) {
                return;
            }
        }

        throw new PossibleStatementsException("The possible statements for predicate: " + predicate + " are between: " + uuStatementsPossibleList1 + ".\n" +
                "Your statement is : " + new UUStatementsPossible(subjectUUID.getNodeType(), predicate, objectUUID.getNodeType()));
    }

}
