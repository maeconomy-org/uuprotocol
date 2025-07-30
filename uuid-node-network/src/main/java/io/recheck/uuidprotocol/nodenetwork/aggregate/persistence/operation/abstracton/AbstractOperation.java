package io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.operation.abstracton;

import lombok.Data;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@Data
public abstract class AbstractOperation<T_Query, T_Update> {

    public abstract Query getQuery(T_Query t);

    public abstract Update getUpdate(T_Update v);

}
