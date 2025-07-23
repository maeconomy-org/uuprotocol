package io.recheck.uuidprotocol.nodenetwork.statements;

import io.recheck.uuidprotocol.domain.node.model.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

public class UUStatementsClassPredicateMap {

    static private MultiValueMap<UUStatementPredicate, UUStatementsClass> map;
    
    static {
        map = new LinkedMultiValueMap<>();
        
        map.add(UUStatementPredicate.IS_PARENT_OF, new UUStatementsClass(UUObject.class, UUStatementPredicate.IS_PARENT_OF, UUObject.class));
        map.add(UUStatementPredicate.IS_CHILD_OF, new UUStatementsClass(UUObject.class, UUStatementPredicate.IS_CHILD_OF, UUObject.class));

        map.add(UUStatementPredicate.IS_INPUT_OF, new UUStatementsClass(UUObject.class, UUStatementPredicate.IS_INPUT_OF, UUObject.class));
        map.add(UUStatementPredicate.IS_OUTPUT_OF, new UUStatementsClass(UUObject.class, UUStatementPredicate.IS_OUTPUT_OF, UUObject.class));

        map.add(UUStatementPredicate.IS_MODEL_OF, new UUStatementsClass(UUObject.class, UUStatementPredicate.IS_MODEL_OF, UUObject.class));
        map.add(UUStatementPredicate.IS_INSTANCE_MODEL_OF, new UUStatementsClass(UUObject.class, UUStatementPredicate.IS_INSTANCE_MODEL_OF, UUObject.class));

        map.add(UUStatementPredicate.IS_PROPERTY_OF, new UUStatementsClass(UUProperty.class, UUStatementPredicate.IS_PROPERTY_OF, UUObject.class));
        map.add(UUStatementPredicate.HAS_PROPERTY, new UUStatementsClass(UUObject.class, UUStatementPredicate.HAS_PROPERTY, UUProperty.class));

        map.add(UUStatementPredicate.IS_VALUE_OF, new UUStatementsClass(UUPropertyValue.class, UUStatementPredicate.IS_VALUE_OF, UUProperty.class));
        map.add(UUStatementPredicate.HAS_VALUE, new UUStatementsClass(UUProperty.class, UUStatementPredicate.HAS_VALUE, UUPropertyValue.class));

        map.add(UUStatementPredicate.IS_FILE_OF, new UUStatementsClass(UUFile.class, UUStatementPredicate.IS_FILE_OF, UUPropertyValue.class));
        map.add(UUStatementPredicate.IS_FILE_OF, new UUStatementsClass(UUFile.class, UUStatementPredicate.IS_FILE_OF, UUProperty.class));
        map.add(UUStatementPredicate.IS_FILE_OF, new UUStatementsClass(UUFile.class, UUStatementPredicate.IS_FILE_OF, UUObject.class));
        map.add(UUStatementPredicate.HAS_FILE, new UUStatementsClass(UUPropertyValue.class, UUStatementPredicate.HAS_FILE, UUFile.class));
        map.add(UUStatementPredicate.HAS_FILE, new UUStatementsClass(UUProperty.class, UUStatementPredicate.HAS_FILE, UUFile.class));
        map.add(UUStatementPredicate.HAS_FILE, new UUStatementsClass(UUObject.class, UUStatementPredicate.HAS_FILE, UUFile.class));

        map.add(UUStatementPredicate.HAS_ADDRESS, new UUStatementsClass(UUObject.class, UUStatementPredicate.HAS_ADDRESS, UUAddress.class));
        map.add(UUStatementPredicate.IS_ADDRESS_OF, new UUStatementsClass(UUAddress.class, UUStatementPredicate.IS_ADDRESS_OF, UUObject.class));
    }
    
    static public List<UUStatementsClass> get(UUStatementPredicate uuStatementPredicate) {
        return map.get(uuStatementPredicate);
    }
    
}
