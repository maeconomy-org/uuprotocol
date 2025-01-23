package io.recheck.uuidprotocol.nodenetwork.model;

import io.recheck.uuidprotocol.nodenetwork.model.audit.Audit;
import lombok.Data;

@Data
public class Node extends Audit {

    private String uuid;

}
