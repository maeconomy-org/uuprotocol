package io.recheck.uuidprotocol.nodenetwork;

import io.recheck.uuidprotocol.common.exceptions.ForbiddenException;
import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUObjectDataSource;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUPropertyDataSource;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUPropertyValueDataSource;
import io.recheck.uuidprotocol.nodenetwork.datasource.UUStatementsDataSource;
import io.recheck.uuidprotocol.nodenetwork.dto.UUObjectDTO;
import io.recheck.uuidprotocol.nodenetwork.dto.UUPropertyDTO;
import io.recheck.uuidprotocol.nodenetwork.dto.UUPropertyValueDTO;
import io.recheck.uuidprotocol.nodenetwork.dto.UUStatementsDTO;
import io.recheck.uuidprotocol.nodenetwork.model.UUObject;
import io.recheck.uuidprotocol.nodenetwork.model.UUProperty;
import io.recheck.uuidprotocol.nodenetwork.model.UUPropertyValue;
import io.recheck.uuidprotocol.nodenetwork.model.UUStatements;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UUIDNodeNetworkService {

    private final UUObjectDataSource uuObjectDataSource;
    private final UUPropertyDataSource uuPropertyDataSource;
    private final UUPropertyValueDataSource uuPropertyValueDataSource;
    private final UUStatementsDataSource uuStatementsDataSource;

    public UUObject findUUObjectByUUID(String uuid) {
        return uuObjectDataSource.findByDocumentId(uuid);
    }

    public List<UUObject> findUUObjectByUUID(List<String> uuidList) {
        return uuObjectDataSource.findByDocumentId(uuidList);
    }

    public List<UUObject> findUUObjectAll() {
        return uuObjectDataSource.findAll();
    }

    public UUObject createOrUpdateUUObject(UUObjectDTO uuObjectDTO, String clientCertFingerprint) {
        return uuObjectDataSource.createOrUpdateAuditCommon(uuObjectDTO.build(), clientCertFingerprint);
    }

    public UUObject softDeleteUUObject(String uuid, String clientCertFingerprint) {
        return uuObjectDataSource.softDeleteAuditCommon(uuid, clientCertFingerprint);
    }

    public UUProperty createOrUpdateUUProperty(UUPropertyDTO uuPropertyDTO, String clientCertFingerprint) {
        return uuPropertyDataSource.createOrUpdateAuditCommon(uuPropertyDTO.build(), clientCertFingerprint);
    }

    public UUProperty softDeleteUUProperty(String propertyId, String clientCertFingerprint) {
        return uuPropertyDataSource.softDeleteAuditCommon(propertyId, clientCertFingerprint);
    }

    public List<UUProperty> findUUPropertyByUUID(String uuid) {
        return uuPropertyDataSource.findByUUObjectUUID(uuid);
    }

    public UUProperty findUUPropertyById(String propertyId) {
        return uuPropertyDataSource.findByDocumentId(propertyId);
    }


    public void validateUUPropertyUUID(String propertyId, String uuid) {
        if (StringUtils.hasText(propertyId)) {
            UUProperty existingUUProperty = uuPropertyDataSource.findByDocumentId(propertyId);
            if (existingUUProperty == null) {
                throw new NotFoundException("Property not found");
            }
            if (!existingUUProperty.getUuid().equals(uuid)) {
                throw new ForbiddenException("The Property does not belong to this UUID");
            }
        }
    }

    public UUPropertyValue createOrUpdateUUPropertyValue(UUPropertyValueDTO uuPropertyValueDTO, String clientCertFingerprint) {
        return uuPropertyValueDataSource.createOrUpdateAuditCommon(uuPropertyValueDTO.build(), clientCertFingerprint);
    }

    public UUPropertyValue softDeleteUUPropertyValue(String valueId, String clientCertFingerprint) {
        return uuPropertyValueDataSource.softDeleteAuditCommon(valueId, clientCertFingerprint);
    }

    public List<UUPropertyValue> findUUPropertyValueByPropertyId(String propertyId) {
        return uuPropertyValueDataSource.findByPropertyId(propertyId);
    }

    public UUPropertyValue findUUPropertyValueById(String valueId) {
        return uuPropertyValueDataSource.findByDocumentId(valueId);
    }

    public void validateUUPropertyValuePropertyId(String propertyValueId, String propertyId) {
        if (StringUtils.hasText(propertyValueId)) {
            UUPropertyValue existingUUPropertyValue = uuPropertyValueDataSource.findByDocumentId(propertyValueId);
            if (existingUUPropertyValue == null) {
                throw new NotFoundException("PropertyValue not found");
            }
            if (!existingUUPropertyValue.getPropertyId().equals(propertyId)) {
                throw new ForbiddenException("The PropertyValue value does not belong to this Property");
            }
        }
    }

    public List<UUStatements> createOrUpdateUUStatements(List<UUStatementsDTO> uuStatementsDTOList, String clientCertFingerprint) {
        List<UUStatements> result = new ArrayList<>();
        for (UUStatementsDTO uuStatementsDTO : uuStatementsDTOList) {
            UUStatements uuStatements = new UUStatements();
            BeanUtils.copyProperties(uuStatementsDTO, uuStatements);
            result.add(uuStatementsDataSource.createOrUpdateAuditCommon(uuStatements, clientCertFingerprint));
        }
        return result;
    }

    public List<UUStatements> findStatementsAll() {
        return uuStatementsDataSource.findAll();
    }

    public UUStatements findStatementsById(String statementsId) {
        return uuStatementsDataSource.findByDocumentId(statementsId);
    }

    public List<UUStatements> findStatementsBySubjectAndPredicate(String subject, String predicate) {
        return uuStatementsDataSource.findBySubjectAndPredicate(subject, predicate);
    }

    public UUStatements softDeleteUUStatements(String statementsId, String clientCertFingerprint) {
        return uuStatementsDataSource.softDeleteAuditCommon(statementsId, clientCertFingerprint);
    }



}
