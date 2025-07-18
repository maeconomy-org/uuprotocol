package io.recheck.uuidprotocol.nodenetwork.aggregate.model;

import io.recheck.uuidprotocol.domain.node.model.UUObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("AggregateEntity")
public class AggregateEntity extends UUObject {

    private List<String> parents;
    private List<String> children;

    private List<String> inputs;
    private List<String> outputs;

    private List<String> models;
    private List<String> instances;

    private List<AggregateFile> files;
    private List<AggregateProperty> properties;

    private List<UUObject> history;

    public static AggregateEntity buildFromUUObject(UUObject uuObject) {
        AggregateEntity aggregateEntity = new AggregateEntity();
        BeanUtils.copyProperties(uuObject, aggregateEntity);
        return aggregateEntity;
    }

}
