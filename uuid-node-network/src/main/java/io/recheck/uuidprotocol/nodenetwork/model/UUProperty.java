package io.recheck.uuidprotocol.nodenetwork.model;

import io.recheck.uuidprotocol.common.model.FirestoreId;
import io.recheck.uuidprotocol.nodenetwork.model.audit.AuditCommon;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UUProperty extends AuditCommon {

    @FirestoreId
    private String id;
    private String uuid;
    private String key;
    private String label;
    private String description;
    private String type;
    private String inputType;
    private String formula;
    private int inputOrderPosition;
    private int processingOrderPosition;
    private int viewOrderPosition;

}
