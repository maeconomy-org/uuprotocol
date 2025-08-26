package io.recheck.uuidprotocol.domain.aggregate.dto.create;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class AggregateUUPropertyValueCreateDTO {

    private String value;
    private String valueTypeCast;
    private String sourceType;
    List<AggregateUUFileCreateDTO> files = new ArrayList<>();

}
