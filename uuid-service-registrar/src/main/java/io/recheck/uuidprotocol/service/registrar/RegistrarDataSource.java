package io.recheck.uuidprotocol.service.registrar;

import com.google.cloud.firestore.Filter;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRecord;
import io.recheck.uuidprotocol.persistence.AuditDataSource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrarDataSource extends AuditDataSource<UUIDRecord> {

    public RegistrarDataSource() {
        super(UUIDRecord.class);
    }

    public List<UUIDRecord> findByOwnerUUID(String ownerUUID) {
        return where(Filter.equalTo("ownerUUID", ownerUUID));
    }

    public UUIDRecord findByUuid(String uuid) {
        return whereFindFirst(Filter.equalTo("uuid", uuid));
    }

}
