package io.recheck.uuidprotocol.nodenetwork.model;

import io.recheck.uuidprotocol.common.model.FirestoreId;
import io.recheck.uuidprotocol.nodenetwork.model.audit.AuditCommon;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UUStatements extends AuditCommon {

    @FirestoreId
    private String id;

    private String subject;
    private String predicate;
    private String object;

}
