package io.recheck.uuidprotocol.nodenetwork.dto;

import io.recheck.uuidprotocol.nodenetwork.model.UUProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.BeanUtils;


@Data
public class UUPropertyDTO {

    private String id;

    @NotBlank
    private String uuid;

    @NotBlank
    private String key;

    private String label;
    private String description;
    private String type;
    private String inputType;
    private String formula;
    private int inputOrderPosition;
    private int processingOrderPosition;
    private int viewOrderPosition;


    public UUProperty build() {
        UUProperty uuProperty = new UUProperty();
        BeanUtils.copyProperties(this, uuProperty);
        return uuProperty;
    }

}
