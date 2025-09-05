package io.recheck.uuidprotocol.domain.node.dto;

import io.recheck.uuidprotocol.common.utils.BeanUtilsCommon;
import io.recheck.uuidprotocol.domain.node.model.UUFile;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UUFileDTO extends NodeDTO<UUFile> {

    private String fileName;

    private String fileReference;

    private String label;

    private String contentType;

    private long size;

    @Override
    public UUFile build() {
        UUFile uuFile = new UUFile();
        BeanUtilsCommon.copyMatchingPropertiesDeep(this, uuFile);
        return uuFile;
    }
}
