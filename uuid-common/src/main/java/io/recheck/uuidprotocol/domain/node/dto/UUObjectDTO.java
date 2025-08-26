package io.recheck.uuidprotocol.domain.node.dto;

import io.recheck.uuidprotocol.common.utils.BeanUtilsCommon;
import io.recheck.uuidprotocol.domain.node.model.UUObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UUObjectDTO extends NodeDTO<UUObject> {

    private String name;

    private String abbreviation;

    private String version;

    private String description;

    public UUObject build() {
        UUObject uuObject = new UUObject();
        BeanUtilsCommon.copyMatchingPropertiesDeep(this, uuObject);
        return uuObject;
    }

}
