package io.recheck.uuidprotocol.domain.node.dto;


import io.recheck.uuidprotocol.domain.node.model.UUPropertyValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

@Data
@EqualsAndHashCode(callSuper=false)
public class UUPropertyValueDTO extends NodeDTO<UUPropertyValue> {

    private String value;

    private String valueTypeCast;

    private String sourceType;

    public UUPropertyValue build() {
        UUPropertyValue uuPropertyValue = new UUPropertyValue();
        BeanUtils.copyProperties(this, uuPropertyValue);
        return uuPropertyValue;
    }

}
