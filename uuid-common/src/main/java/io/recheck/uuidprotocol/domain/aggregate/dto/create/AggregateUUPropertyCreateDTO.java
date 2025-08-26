package io.recheck.uuidprotocol.domain.aggregate.dto.create;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class AggregateUUPropertyCreateDTO {

    private String key;

    private String version;
    private String label;
    private String description;
    private String type;
    private String inputType;
    private String formula;
    private int inputOrderPosition;
    private int processingOrderPosition;
    private int viewOrderPosition;

    List<AggregateUUPropertyValueCreateDTO> values = new ArrayList<>();
    List<AggregateUUFileCreateDTO> files = new ArrayList<>();

}
