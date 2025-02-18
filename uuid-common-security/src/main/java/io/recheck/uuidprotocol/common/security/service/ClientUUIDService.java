package io.recheck.uuidprotocol.common.security.service;

import io.recheck.uuidprotocol.common.exceptions.ForbiddenException;
import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.common.security.datasource.ClientUUIDDataSource;
import io.recheck.uuidprotocol.common.security.dto.UUIDDTO;
import io.recheck.uuidprotocol.common.security.model.ClientUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientUUIDService {

    private final ClientUUIDDataSource clientUUIDDataSource;

    public UUIDDTO createUUID(String clientCertFingerprint) {
        String uuid = UUID.randomUUID().toString();
        clientUUIDDataSource.createOrUpdate(new ClientUUID(uuid, clientCertFingerprint));
        return new UUIDDTO(uuid);
    }

    public List<ClientUUID> findAll() {
        return clientUUIDDataSource.findAll();
    }

    public List<UUIDDTO> findAllCastUUIDDTO() {
        return clientUUIDDataSource.findAll(UUIDDTO.class);
    }

    public List<ClientUUID> findByClient(String clientCertFingerprint) {
        return clientUUIDDataSource.findByClientCertFingerprint(clientCertFingerprint);
    }

    public List<UUIDDTO> findByClientCastUUIDDTO(String clientCertFingerprint) {
        return clientUUIDDataSource.findByClientCertFingerprintCastUUIDDTO(clientCertFingerprint);
    }

    public void validateClientUUID(String clientCertFingerprint, String uuid) {
        validateClientUUID(clientCertFingerprint, clientUUIDDataSource.findByUUID(uuid));
    }

    public void validateClientUUID(String clientCertFingerprint, List<String> uuidList) {
        clientUUIDDataSource.findByUUIDList(uuidList).forEach(clientUUID -> validateClientUUID(clientCertFingerprint, clientUUID));
    }

    public void validateClientUUID(String clientCertFingerprint, ClientUUID existingUUID) {
        if (existingUUID == null) {
            throw new NotFoundException("UUID not found");
        }
        if (!existingUUID.getClientCertFingerprint().equals(clientCertFingerprint)) {
            throw new ForbiddenException("The UUID does not belong to this Client");
        }
    }

}
