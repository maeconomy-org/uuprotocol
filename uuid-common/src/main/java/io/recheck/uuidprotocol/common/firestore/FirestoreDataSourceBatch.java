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

    private WriteBatch writeBatch;
    private int counter = 0;

    @SneakyThrows
    public <T_COLLECTION> T_COLLECTION createOrUpdate(T_COLLECTION pojo) {
        CollectionReference collectionReference = firestore.collection(pojo.getClass().getSimpleName());
        DocumentReference documentReference = collectionReference.document();
        setId(pojo, documentReference.getId());
        if (writeBatch == null) {
            writeBatch = firestore.batch();
        }
        writeBatch.set(documentReference, pojo);
        if (counter < 200) {
            counter++;
        } else {
            commitBatch();
        }
        return pojo;
    }

    public void commitBatch() {
        if (counter > 0) {
            try {
                // Commit the batch and wait for the result
                ApiFuture<List<WriteResult>> future = writeBatch.commit();
                List<WriteResult> results = future.get(); // Blocks until the batch completes
                System.out.println("Batch committed successfully!");
                writeBatch = firestore.batch();
                counter = 0;
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
