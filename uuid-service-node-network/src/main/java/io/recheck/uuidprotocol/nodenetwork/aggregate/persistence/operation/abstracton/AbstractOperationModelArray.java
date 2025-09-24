package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.abstracton;


import lombok.Getter;

public class AbstractOperationModelArray<T_UPDATE_OBJECT,T_ARRAY_CRITERIA> extends AbstractOperationModel<T_UPDATE_OBJECT> {

    @Getter
    private final T_ARRAY_CRITERIA arrayCriteriaObject;

    public AbstractOperationModelArray(T_UPDATE_OBJECT t_update_object, T_ARRAY_CRITERIA arrayCriteriaObject) {
        super(t_update_object);
        this.arrayCriteriaObject = arrayCriteriaObject;
    }
}
