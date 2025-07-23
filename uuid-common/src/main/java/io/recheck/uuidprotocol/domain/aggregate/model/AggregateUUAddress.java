package io.recheck.uuidprotocol.domain.aggregate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AggregateUUAddress extends AggregateNode {

    private String fullAddress;
    private String street;
    private String houseNumber;
    private String city;
    private String postalCode;
    private String country;
    private String state;
    private String district;

}
