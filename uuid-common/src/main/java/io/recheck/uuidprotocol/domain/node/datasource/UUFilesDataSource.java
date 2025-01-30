package io.recheck.uuidprotocol.domain.node.datasource;

import io.recheck.uuidprotocol.domain.node.model.UUFiles;
import org.springframework.stereotype.Service;

@Service
public class UUFilesDataSource extends NodeDataSource<UUFiles> {
    public UUFilesDataSource() {
        super(UUFiles.class);
    }
}
