package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener;

import io.recheck.uuidprotocol.domain.audit.Audit;

public interface AggregateAuditEventListener<T extends Audit> {
    void postCreate(T pojoAudit);
    void postUpdate(T pojoAudit);
    void postSoftDelete(T pojoAudit);
}
