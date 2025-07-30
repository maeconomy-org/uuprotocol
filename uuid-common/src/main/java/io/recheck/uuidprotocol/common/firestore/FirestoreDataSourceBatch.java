package io.recheck.uuidprotocol.common.firestore;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import io.recheck.uuidprotocol.common.firestore.model.FirestoreId;
import io.recheck.uuidprotocol.common.utils.ReflectionUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FirestoreDataSourceBatch {

    @Autowired
    protected Firestore firestore;

    @SneakyThrows
    public <T_COLLECTION> T_COLLECTION createOrUpdate(T_COLLECTION pojo, FirestoreDataSourceBatchModel batchModel) {
        CollectionReference collectionReference = firestore.collection(pojo.getClass().getSimpleName());
        DocumentReference documentReference = collectionReference.document();
        setId(pojo, documentReference.getId());
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

    public WriteBatch getWriteBatch() {
        return firestore.batch();
    }

    public void commitBatch(FirestoreDataSourceBatchModel batchModel) {
        if (batchModel.getCounter() > 0) {
            try {
                // Commit the batch and wait for the result
                ApiFuture<List<WriteResult>> future = batchModel.getWriteBatch().commit();
                List<WriteResult> results = future.get(); // Blocks until the batch completes
                System.out.println("Batch committed successfully!");
                batchModel.setWriteBatch(firestore.batch());
                batchModel.setCounter(0);
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error committing batch: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    private void setId(Object object, String id) {
        ReflectionUtils.setValueAnnotationPresent(FirestoreId.class, object, id);
    }


}
