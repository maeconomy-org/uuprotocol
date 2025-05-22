package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operations;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AbstractBinaryOperation<T extends AbstractOperation, V extends AbstractOperation> {

    private T createStatement;
    private V deleteStatement;

}
