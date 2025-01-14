package io.recheck.uuidprotocol.nodenetwork.dto;

import io.recheck.uuidprotocol.nodenetwork.model.UUObject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UUObjectDTO {

    @NotNull
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
    private String uuid;

    private String name;

    private String abbreviation;

    private String version;

    private String description;

    public UUObject build() {
        UUObject uuObject = new UUObject();
        BeanUtils.copyProperties(this, uuObject);
        return uuObject;
    }

}
