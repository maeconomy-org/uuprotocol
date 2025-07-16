package io.recheck.uuidprotocol.nodenetwork.aggregate.imports;

import io.recheck.uuidprotocol.common.exceptions.ForbiddenException;
import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.domain.owner.model.UUIDOwner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UUIDOwnerImportService {

    private final UUIDOwnerImportDataSource uuidOwnerImportDataSource;

    public UUIDOwner createUUID(String certFingerprint) {
        String uuid = UUID.randomUUID().toString();
        return uuidOwnerImportDataSource.createOrUpdate(new UUIDOwner(uuid, certFingerprint));
    }

    public List<UUIDOwner> findAll() {
        return uuidOwnerImportDataSource.findAll();
    }

    public UUIDOwner findByUUID(String uuid) {
        return uuidOwnerImportDataSource.findByUUID(uuid);
    }

    public List<UUIDOwner> findByOwner(String certFingerprint) {
        return uuidOwnerImportDataSource.findByCertFingerprint(certFingerprint);
    }

    public UUIDOwner updateNodeType(String uuid, String nodeType) {
        UUIDOwner existingUUID = findByUUID(uuid);
        if (existingUUID == null) {
            throw new NotFoundException("UUID not found");
        }
        existingUUID.setNodeType(nodeType);
        return uuidOwnerImportDataSource.createOrUpdate(existingUUID);
    }

    public UUIDOwner validateOwnerUUID(String certFingerprint, String uuid) {
        UUIDOwner existingUUID = findByUUID(uuid);
        if (existingUUID == null) {
            throw new NotFoundException("UUID not found");
        }
        if (!existingUUID.getCertFingerprint().equals(certFingerprint)) {
            throw new ForbiddenException("The UUID does not belong to this client");
        }
        return existingUUID;
    }

}
