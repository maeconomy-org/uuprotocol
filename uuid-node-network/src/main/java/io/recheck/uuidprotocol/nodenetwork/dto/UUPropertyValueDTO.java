package io.recheck.uuidprotocol.nodenetwork.dto;


import io.recheck.uuidprotocol.nodenetwork.model.UUPropertyValue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UUPropertyValueDTO {

    private String id;

    @NotBlank
    private String propertyId;

    @NotBlank
    private String value;

    private String valueTypeCast;
    private String sourceType;

    public UUPropertyValue build() {
        UUPropertyValue uuPropertyValue = new UUPropertyValue();
        BeanUtils.copyProperties(this, uuPropertyValue);
        return uuPropertyValue;
    }

}
