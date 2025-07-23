package io.recheck.uuidprotocol.nodenetwork.audit;

import io.recheck.uuidprotocol.domain.node.model.audit.Audit;

public interface AuditEventListener<T extends Audit> {
    void postCreate(T pojoAudit);
    void postUpdate(T pojoAudit);
    void postSoftDelete(T pojoAudit);
}
