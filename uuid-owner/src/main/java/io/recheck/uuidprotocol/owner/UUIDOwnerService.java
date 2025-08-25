package io.recheck.uuidprotocol.owner;

import io.recheck.uuidprotocol.common.exceptions.ForbiddenException;
import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.domain.owner.model.UUIDOwner;
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
        return uuidOwnerDataSource.create(new UUIDOwner(uuid, certFingerprint));
    }

    public UUIDOwner findByUUID(String uuid) {
        return uuidOwnerDataSource.findByUUID(uuid);
    }

    public List<UUIDOwner> findByOwner(String certFingerprint) {
        return uuidOwnerDataSource.findByCertFingerprint(certFingerprint);
    }

    public UUIDOwner validateOwnerUUID(String certFingerprint, String uuid) {
        UUIDOwner existingUUIDOwner = findByUUID(uuid);
        if (existingUUIDOwner == null) {
            throw new NotFoundException("UUID not found");
        }
        if (!existingUUIDOwner.getCertFingerprint().equals(certFingerprint)) {
            throw new ForbiddenException("The UUID does not belong to this client");
        }
        return existingUUIDOwner;
    }

}
