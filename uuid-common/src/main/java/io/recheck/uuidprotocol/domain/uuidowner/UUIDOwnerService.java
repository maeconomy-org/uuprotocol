package io.recheck.uuidprotocol.domain.uuidowner;

import io.recheck.uuidprotocol.common.exceptions.ForbiddenException;
import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UUIDOwnerService {

    private final UUIDOwnerDataSource uuidOwnerDataSource;

    public UUIDOwner createUUID(String certFingerprint) {
        String uuid = UUID.randomUUID().toString();
        return uuidOwnerDataSource.createOrUpdate(new UUIDOwner(uuid, certFingerprint));
    }

    public List<UUIDOwner> findAll() {
        return uuidOwnerDataSource.findAll();
    }

    public UUIDOwner findByUUID(String uuid) {
        return uuidOwnerDataSource.findByUUID(uuid);
    }

    public List<UUIDOwner> findByOwner(String certFingerprint) {
        return uuidOwnerDataSource.findByCertFingerprint(certFingerprint);
    }

    public UUIDOwner updateNodeType(UUIDOwner existingUUID, String nodeType) {
        existingUUID.setNodeType(nodeType);
        return uuidOwnerDataSource.createOrUpdate(existingUUID);
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
