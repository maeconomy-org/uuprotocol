package io.recheck.uuidprotocol.domain.node.dto;


import io.recheck.uuidprotocol.domain.node.model.UUPropertyValue;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
public class UUPropertyValueDTO {

    private String value;

    private List<String> uuFilesUUID;

    private String valueTypeCast;

    private String sourceType;

    public UUPropertyValue build() {
        UUPropertyValue uuPropertyValue = new UUPropertyValue();
        BeanUtils.copyProperties(this, uuPropertyValue);
        return uuPropertyValue;
    }

}
