package io.recheck.uuidprotocol.domain.uuidowner;

import io.recheck.uuidprotocol.common.exceptions.ForbiddenException;
import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerUUIDService {

    private final OwnerUUIDDataSource ownerUUIDDataSource;

    public OwnerUUIDDTO createUUID(String ownerCertFingerprint) {
        String uuid = UUID.randomUUID().toString();
        ownerUUIDDataSource.createOrUpdate(new OwnerUUID(uuid, ownerCertFingerprint));
        return new OwnerUUIDDTO(uuid);
    }

    public List<OwnerUUID> findAll() {
        return ownerUUIDDataSource.findAll();
    }

    public List<OwnerUUIDDTO> findAllCastUUIDDTO() {
        return ownerUUIDDataSource.findAll(OwnerUUIDDTO.class);
    }

    public List<OwnerUUID> findByOwner(String ownerCertFingerprint) {
        return ownerUUIDDataSource.findByOwnerCertFingerprint(ownerCertFingerprint);
    }

    public List<OwnerUUIDDTO> findByOwnerCastUUIDDTO(String ownerCertFingerprint) {
        return ownerUUIDDataSource.findByOwnerCertFingerprintCastUUIDDTO(ownerCertFingerprint);
    }

    public void validateOwnerUUID(String ownerCertFingerprint, String uuid) {
        OwnerUUID existingUUID = ownerUUIDDataSource.findByUUID(uuid);
        if (existingUUID == null) {
            throw new NotFoundException("UUID not found");
        }
        if (!existingUUID.getOwnerCertFingerprint().equals(ownerCertFingerprint)) {
            throw new ForbiddenException("The UUID does not belong to this client");
        }
    }

}
