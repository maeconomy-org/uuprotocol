package io.recheck.uuidprotocol.common.firestore;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class FirestoreDataSourceBatch {

    @Autowired
    protected Firestore firestore;

    @SneakyThrows
    public <T_COLLECTION> T_COLLECTION create(T_COLLECTION pojo, FirestoreDataSourceBatchModel batchModel) {
        CollectionReference collectionReference = firestore.collection(pojo.getClass().getSimpleName());
        DocumentReference documentReference = collectionReference.document();
        if (batchModel.getWriteBatch() == null) {
            batchModel.setWriteBatch(firestore.batch());
        }
        batchModel.getWriteBatch().set(documentReference, pojo);
        if (batchModel.getCounter() < 400) {
            batchModel.setCounter(batchModel.getCounter()+1);
        } else {
            commitBatch(batchModel);
        }
        return pojo;
    }

    public void commitBatch(FirestoreDataSourceBatchModel batchModel) {
        if (batchModel.getCounter() > 0) {
            try {
                log.info("commit batch with {} count of objects", batchModel.getCounter());
                ApiFuture<List<WriteResult>> future = batchModel.getWriteBatch().commit();
                future.get(); // Blocks until the batch completes
                log.info("Batch committed successfully!");
                batchModel.setWriteBatch(firestore.batch());
                batchModel.setCounter(0);
            } catch (InterruptedException | ExecutionException e) {
                log.info("Error committing batch: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


}
