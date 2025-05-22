package io.recheck.uuidprotocol.nodenetwork.aggregate.model;

import io.recheck.uuidprotocol.domain.node.model.UUProperty;
import lombok.Data;

import java.util.List;

@Data
public class AggregateProperty extends UUProperty {

    List<AggregatePropertyValue> values;
    List<AggregateFile> files;

}
