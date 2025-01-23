package io.recheck.uuidprotocol.nodenetwork.model;

import io.recheck.uuidprotocol.common.model.FirestoreId;
import lombok.Data;

@Data
public class BaseEntity {

    @FirestoreId
    private String id;

}
