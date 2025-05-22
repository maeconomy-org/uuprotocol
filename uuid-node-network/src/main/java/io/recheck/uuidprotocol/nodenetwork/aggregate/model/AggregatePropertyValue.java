package io.recheck.uuidprotocol.nodenetwork.aggregate.model;

import io.recheck.uuidprotocol.domain.node.model.UUPropertyValue;
import lombok.Data;

import java.util.List;

@Data
public class AggregatePropertyValue extends UUPropertyValue {

    List<AggregateFile> files;

}
