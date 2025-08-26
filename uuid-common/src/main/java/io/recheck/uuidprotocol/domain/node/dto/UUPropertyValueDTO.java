package io.recheck.uuidprotocol.domain.node.dto;


import io.recheck.uuidprotocol.common.utils.BeanUtilsCommon;
import io.recheck.uuidprotocol.domain.node.model.UUPropertyValue;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UUPropertyValueDTO extends NodeDTO<UUPropertyValue> {

    private String value;

    private String valueTypeCast;

    private String sourceType;

    public UUPropertyValue build() {
        UUPropertyValue uuPropertyValue = new UUPropertyValue();
        BeanUtilsCommon.copyMatchingPropertiesDeep(this, uuPropertyValue);
        return uuPropertyValue;
    }

}
