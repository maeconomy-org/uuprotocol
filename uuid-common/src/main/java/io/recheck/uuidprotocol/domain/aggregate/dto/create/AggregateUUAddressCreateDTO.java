package io.recheck.uuidprotocol.domain.aggregate.dto.create;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AggregateUUAddressCreateDTO {

    private String fullAddress;
    private String street;
    private String houseNumber;
    private String city;
    private String postalCode;
    private String country;
    private String state;
    private String district;

}
