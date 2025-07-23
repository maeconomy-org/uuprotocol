package io.recheck.uuidprotocol.domain.node.dto;

import io.recheck.uuidprotocol.domain.node.model.UUAddress;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

@Data
@EqualsAndHashCode(callSuper=false)
public class UUAddressDTO extends NodeDTO<UUAddress> {

    private String fullAddress;
    private String street;
    private String houseNumber;
    private String city;
    private String postalCode;
    private String country;
    private String state;
    private String district;

    @Override
    public UUAddress build() {
        UUAddress uuAddress = new UUAddress();
        BeanUtils.copyProperties(this, uuAddress);
        return uuAddress;
    }
}
