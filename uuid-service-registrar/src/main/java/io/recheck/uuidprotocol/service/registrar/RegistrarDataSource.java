package io.recheck.uuidprotocol.service.registrar;

import com.google.cloud.firestore.Filter;
import io.recheck.uuidprotocol.domain.audit.AuditUser;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRecord;
import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;
import io.recheck.uuidprotocol.persistence.AuditDataSource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class RegistrarDataSource extends AuditDataSource<UUIDRecord> {

    public RegistrarDataSource() {
        super(UUIDRecord.class);
    }

    public UUIDRecord createAudit(UUIDRecord pojoAudit, UserDetailsCustom user) {
        Instant now = Instant.now();
        pojoAudit.setCreatedAt(now);
        pojoAudit.setCreatedBy(new AuditUser(user));
        pojoAudit.setLastUpdatedAt(now);
        pojoAudit.setLastUpdatedBy(new AuditUser(user));
        return createOrUpdate(pojoAudit);
    }

    public List<UUIDRecord> findByOwnerUUID(String ownerUUID) {
        return where(Filter.equalTo("ownerUUID", ownerUUID));
    }

    public UUIDRecord findByUuid(String uuid) {
        return whereFindFirst(Filter.equalTo("uuid", uuid));
    }

}
