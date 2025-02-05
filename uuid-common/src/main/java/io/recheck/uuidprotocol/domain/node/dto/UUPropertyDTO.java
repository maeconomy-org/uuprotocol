package io.recheck.uuidprotocol.domain.node.dto;

import io.recheck.uuidprotocol.domain.node.model.UUProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

@Data
@EqualsAndHashCode(callSuper=false)
public class UUPropertyDTO extends NodeDTO<UUProperty> {

    @NotBlank
    private String key;

    private String version;

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
