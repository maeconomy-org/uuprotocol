package io.recheck.uuidprotocol.domain.node.dto;

import io.recheck.uuidprotocol.domain.node.model.UUObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

@Data
@EqualsAndHashCode(callSuper=false)
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
