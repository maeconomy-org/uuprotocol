package io.recheck.uuidprotocol.nodenetwork.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.recheck.uuidprotocol.nodenetwork.model.UUProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;


@Data
public class UUPropertyDTO extends NodeDTO<UUProperty> {

    @NotNull
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
    private String uuid;

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

    @JsonProperty("propertyValues")
    private List<UUPropertyValueDTO> propertyValuesDTO;


    public UUProperty build() {
        UUProperty uuProperty = new UUProperty();
        BeanUtils.copyProperties(this, uuProperty);

        if (propertyValuesDTO != null && !propertyValuesDTO.isEmpty()) {
            uuProperty.setPropertyValues(new ArrayList<>());
            for (UUPropertyValueDTO uuPropertyValueDTO : propertyValuesDTO) {
                uuProperty.getPropertyValues().add(uuPropertyValueDTO.build());
            }
        }

        return uuProperty;
    }

}
