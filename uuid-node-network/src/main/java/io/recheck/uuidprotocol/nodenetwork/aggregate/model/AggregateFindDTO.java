package io.recheck.uuidprotocol.nodenetwork.aggregate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregateFindDTO {

    private int page = 0;
    private int size = 5;

    private String createdBy;

    private Boolean hasChildrenFull = false;

    private Boolean hasHistory = false;

}
