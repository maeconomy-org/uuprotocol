package io.recheck.uuidprotocol.common.firestore.index.model;

import lombok.Data;

import java.util.List;

@Data
public class ListIndexes {
    private List<IndexResponse> indexes;
    private String nextPageToken;
}
