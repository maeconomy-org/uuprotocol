package io.recheck.uuidprotocol.nodenetwork.aggregate.imports;

import io.recheck.uuidprotocol.domain.owner.model.UUIDOwner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UUIDOwnerImportService {

    private final UUIDOwnerImportDataSource uuidOwnerImportDataSource;

    public UUIDOwner createUUID(String certFingerprint, String nodeType) {
        String uuid = UUID.randomUUID().toString();
        UUIDOwner uuidOwner = new UUIDOwner(uuid, certFingerprint);
        uuidOwner.setNodeType(nodeType);
        return uuidOwnerImportDataSource.createOrUpdate(uuidOwner);
    }

}
