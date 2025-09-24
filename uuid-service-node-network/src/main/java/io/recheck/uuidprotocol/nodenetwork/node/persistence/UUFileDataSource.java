package io.recheck.uuidprotocol.nodenetwork.node.persistence;

import io.recheck.uuidprotocol.domain.node.model.UUFile;
import org.springframework.stereotype.Service;

@Service
public class UUFileDataSource extends NodeDataSource<UUFile> {
    public UUFileDataSource() {
        super(UUFile.class);
    }
}
