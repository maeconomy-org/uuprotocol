package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.abstracton;

import lombok.Data;

@Data
public class AbstractOperationModel<T_UPDATE_OBJECT> {

    private final T_UPDATE_OBJECT updateObject;

}