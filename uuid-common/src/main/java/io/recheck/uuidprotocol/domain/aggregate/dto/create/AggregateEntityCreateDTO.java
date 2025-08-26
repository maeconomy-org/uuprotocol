package io.recheck.uuidprotocol.domain.aggregate.dto.create;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class AggregateEntityCreateDTO extends AggregateUUObjectCreateDTO {

    private AggregateUUAddressCreateDTO address;
    private List<AggregateUUFileCreateDTO> files;
    private List<AggregateUUPropertyCreateDTO> properties = new ArrayList<>();

}
