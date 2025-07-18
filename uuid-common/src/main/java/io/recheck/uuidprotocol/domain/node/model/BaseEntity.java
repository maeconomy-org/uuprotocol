package io.recheck.uuidprotocol.domain.node.model;

import io.recheck.uuidprotocol.common.firestore.model.FirestoreId;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class BaseEntity {

    @FirestoreId
    private String id;

}
