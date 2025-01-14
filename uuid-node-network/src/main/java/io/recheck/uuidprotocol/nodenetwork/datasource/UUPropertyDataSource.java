package io.recheck.uuidprotocol.nodenetwork.datasource;

import io.recheck.uuidprotocol.nodenetwork.model.UUProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UUPropertyDataSource extends AuditDataSource<UUProperty> {
    public UUPropertyDataSource() {
        super(UUProperty.class);
    }

    public List<UUProperty> findByUUObjectUUID(String uuid) {
        return whereEqualTo("uuid", uuid);
    }

}
