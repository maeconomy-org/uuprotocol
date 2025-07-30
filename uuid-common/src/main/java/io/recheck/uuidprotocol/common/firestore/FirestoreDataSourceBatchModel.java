package io.recheck.uuidprotocol.common.firestore;

import com.google.cloud.firestore.WriteBatch;
import lombok.Data;

@Data
public class FirestoreDataSourceBatchModel {
    private WriteBatch writeBatch;
    private int counter = 0;
}
