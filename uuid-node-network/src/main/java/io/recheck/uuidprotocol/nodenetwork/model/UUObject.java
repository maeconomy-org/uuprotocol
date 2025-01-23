package io.recheck.uuidprotocol.nodenetwork.model;

import io.recheck.uuidprotocol.common.model.FirestoreId;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UUObject extends Node {

    @FirestoreId
    private String uuid;

    private String name;

    private String abbreviation;

    private String version;

    private String description;

}
