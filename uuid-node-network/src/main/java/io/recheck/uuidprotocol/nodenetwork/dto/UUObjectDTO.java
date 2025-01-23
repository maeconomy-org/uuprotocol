package io.recheck.uuidprotocol.nodenetwork.dto;

import io.recheck.uuidprotocol.nodenetwork.model.UUObject;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UUObjectDTO extends NodeDTO<UUObject> {

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
