package io.recheck.uuidprotocol.common.firestore.index.model;

import lombok.Data;

import java.util.List;

@Data
public class IndexRequest {
    private final QueryScope queryScope;
    private final List<IndexField> fields;
}
