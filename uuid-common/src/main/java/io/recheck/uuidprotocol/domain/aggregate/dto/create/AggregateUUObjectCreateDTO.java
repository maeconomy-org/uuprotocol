package io.recheck.uuidprotocol.domain.aggregate.dto.create;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AggregateUUObjectCreateDTO {

    private String name;
    private String abbreviation;
    private String version;
    private String description;

}
