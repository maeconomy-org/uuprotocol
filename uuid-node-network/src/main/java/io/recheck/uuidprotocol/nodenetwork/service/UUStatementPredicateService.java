package io.recheck.uuidprotocol.nodenetwork.service;

import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.domain.node.datasource.*;
import io.recheck.uuidprotocol.domain.node.model.*;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

@Service
public class UUStatementPredicateService {

    private final UUPropertyDataSource uuPropertyDataSource;
    private final UUPropertyValueDataSource uuPropertyValueDataSource;
    private final UUObjectDataSource uuObjectDataSource;
    private final UUFileDataSource uuFileDataSource;
    private final MultiValueMap<UUStatementPredicate, StatementValidate> possibleStatements;

    public UUStatementPredicateService(UUPropertyDataSource uuPropertyDataSource, UUPropertyValueDataSource uuPropertyValueDataSource, UUObjectDataSource uuObjectDataSource, UUFileDataSource uuFileDataSource) {
        this.uuPropertyDataSource = uuPropertyDataSource;
        this.uuPropertyValueDataSource = uuPropertyValueDataSource;
        this.uuObjectDataSource = uuObjectDataSource;
        this.uuFileDataSource = uuFileDataSource;

        possibleStatements = MultiValueMap.fromMultiValue(
                Map.ofEntries(
                        Map.entry(UUStatementPredicate.IS_PARENT_OF, List.of(new StatementValidate<>(uuObjectDataSource, UUObject.class, uuObjectDataSource, UUObject.class))),
                        Map.entry(UUStatementPredicate.IS_CHILD_OF, List.of(new StatementValidate<>(uuObjectDataSource, UUObject.class, uuObjectDataSource, UUObject.class))),

                        Map.entry(UUStatementPredicate.IS_INPUT_OF, List.of(new StatementValidate<>(uuObjectDataSource, UUObject.class, uuObjectDataSource, UUObject.class))),
                        Map.entry(UUStatementPredicate.IS_OUTPUT_OF, List.of(new StatementValidate<>(uuObjectDataSource, UUObject.class, uuObjectDataSource, UUObject.class))),

                        Map.entry(UUStatementPredicate.IS_MODEL_OF, List.of(new StatementValidate<>(uuObjectDataSource, UUObject.class, uuObjectDataSource, UUObject.class))),
                        Map.entry(UUStatementPredicate.IS_INSTANCE_MODEL_OF, List.of(new StatementValidate<>(uuObjectDataSource, UUObject.class, uuObjectDataSource, UUObject.class))),

                        Map.entry(UUStatementPredicate.IS_PROPERTY_OF, List.of(new StatementValidate<>(uuPropertyDataSource, UUProperty.class, uuObjectDataSource, UUObject.class))),
                        Map.entry(UUStatementPredicate.HAS_PROPERTY, List.of(new StatementValidate<>(uuObjectDataSource, UUObject.class, uuPropertyDataSource, UUProperty.class))),

                        Map.entry(UUStatementPredicate.IS_VALUE_OF, List.of(new StatementValidate<>(uuPropertyValueDataSource, UUPropertyValue.class, uuPropertyDataSource, UUProperty.class))),
                        Map.entry(UUStatementPredicate.HAS_VALUE, List.of(new StatementValidate<>(uuPropertyDataSource, UUProperty.class, uuPropertyValueDataSource, UUPropertyValue.class))),

                        Map.entry(UUStatementPredicate.IS_FILE_OF, List.of(new StatementValidate<>(uuFileDataSource, UUFile.class, uuPropertyValueDataSource, UUPropertyValue.class),
                                new StatementValidate<>(uuFileDataSource, UUFile.class, uuPropertyDataSource, UUProperty.class),
                                new StatementValidate<>(uuFileDataSource, UUFile.class, uuObjectDataSource, UUObject.class))),
                        Map.entry(UUStatementPredicate.HAS_FILE, List.of(new StatementValidate<>(uuPropertyValueDataSource, UUPropertyValue.class, uuFileDataSource, UUFile.class),
                                new StatementValidate<>(uuPropertyDataSource, UUProperty.class, uuFileDataSource, UUFile.class),
                                new StatementValidate<>(uuObjectDataSource, UUObject.class, uuFileDataSource, UUFile.class)))
                )
        );
    }

    @Data
    private class StatementValidate<T extends Node, U extends Node> {
        private final NodeDataSource<T> dataSourceSubject;
        private final Class<T> subjectType;
        private final NodeDataSource<U> dataSourceObject;
        private final Class<U> objectType;

        public void validateStatement(String subject, String object) {
            if (!dataSourceSubject.exist(subject)) {
                throw new NotFoundException("Statement subject " + subjectType.getSimpleName() + " not found with uuid=" + subject);
            }
            if (!dataSourceObject.exist(object)) {
                throw new NotFoundException("Statement object " + objectType.getSimpleName() + " not found with uuid=" + object);
            }
        }
    }


    public void validateStatement(String subject, UUStatementPredicate predicate, String object) {
        List<StatementValidate> statementValidateList = possibleStatements.get(predicate);
        for (StatementValidate statementValidate : statementValidateList) {
            statementValidate.validateStatement(subject, object);
        }
    }

}
