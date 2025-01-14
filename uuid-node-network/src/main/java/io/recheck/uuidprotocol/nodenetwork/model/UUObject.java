package io.recheck.uuidprotocol.nodenetwork.model;

import io.recheck.uuidprotocol.common.model.FirestoreId;
import io.recheck.uuidprotocol.nodenetwork.model.audit.AuditCommon;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UUObject extends AuditCommon {

    @FirestoreId
    private String uuid;

    private String name;

    private String abbreviation;

    private String version;

    private String description;

}
