package io.recheck.uuidprotocol.service.registrar;

import io.recheck.uuidprotocol.common.firestore.model.WrapUnaryEqualToFilter;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRecord;
import io.recheck.uuidprotocol.persistence.AuditDataSource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrarDataSource extends AuditDataSource<UUIDRecord> {

    WrapUnaryEqualToFilter ownerUuidFilter = new WrapUnaryEqualToFilter("ownerUUID");
    WrapUnaryEqualToFilter uuidFilter = new WrapUnaryEqualToFilter("uuid");

    public RegistrarDataSource() {
        super(UUIDRecord.class);
    }

    public List<UUIDRecord> findByOwnerUUID(String ownerUUID) {
        return where(ownerUuidFilter.toFirestoreFilter(ownerUUID));
    }

    public UUIDRecord findByUuid(String uuid) {
        return whereFindFirst(uuidFilter.toFirestoreFilter(uuid));
    }

}