package io.recheck.uuidprotocol.domain.node.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UUAddress extends Node {

    private String fullAddress;
    private String street;
    private String houseNumber;
    private String city;
    private String postalCode;
    private String country;
    private String state;
    private String district;

}
