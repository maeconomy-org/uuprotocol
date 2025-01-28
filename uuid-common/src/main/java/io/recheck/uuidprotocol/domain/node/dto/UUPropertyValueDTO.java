package io.recheck.uuidprotocol.domain.node.dto;


import io.recheck.uuidprotocol.domain.node.model.UUPropertyValue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UUPropertyValueDTO {

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
