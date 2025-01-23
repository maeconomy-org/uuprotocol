package io.recheck.uuidprotocol.nodenetwork.dto;

import io.recheck.uuidprotocol.nodenetwork.model.Node;
import lombok.Data;

@Data
public abstract class NodeDTO<T extends Node> {

    private String uuid;
    public abstract T build();

}
