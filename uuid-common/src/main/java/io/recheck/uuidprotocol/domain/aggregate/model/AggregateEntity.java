package io.recheck.uuidprotocol.domain.aggregate.model;

import io.recheck.uuidprotocol.domain.node.model.UUObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document("AggregateEntity")
@EqualsAndHashCode(callSuper=false)
public class AggregateEntity extends AggregateUUObject {

    private AggregateUUAddress address;

    private List<String> parents = new ArrayList<>();
    private List<String> children = new ArrayList<>();
    private List<AggregateEntity> childrenFull = new ArrayList<>();

    private List<String> inputs = new ArrayList<>();
    private List<String> outputs = new ArrayList<>();

    private List<String> models = new ArrayList<>();
    private List<String> instances = new ArrayList<>();

    private List<AggregateUUFile> files = new ArrayList<>();
    private List<AggregateUUProperty> properties = new ArrayList<>();

    private List<AggregateUUObject> history = new ArrayList<>();

    public static AggregateEntity buildFromUUObject(UUObject uuObject) {
        AggregateEntity aggregateEntity = new AggregateEntity();
        BeanUtils.copyProperties(uuObject, aggregateEntity);
        return aggregateEntity;
    }

}
