package io.recheck.uuidprotocol.common.firestore.index.model;

import com.google.cloud.firestore.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexField {
    public String fieldPath;
    public Query.Direction order = Query.Direction.ASCENDING;

    public IndexField(String fieldPath) {
        this.fieldPath = fieldPath;
    }
}