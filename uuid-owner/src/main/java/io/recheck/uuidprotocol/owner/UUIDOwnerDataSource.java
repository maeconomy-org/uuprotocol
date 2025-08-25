package io.recheck.uuidprotocol.owner;

import com.google.cloud.firestore.Filter;
import io.recheck.uuidprotocol.common.firestore.FirestoreDataSource;
import io.recheck.uuidprotocol.domain.owner.model.UUIDOwner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UUIDOwnerDataSource extends FirestoreDataSource<UUIDOwner> {

    public UUIDOwnerDataSource() {
        super(UUIDOwner.class);
    }

    public List<UUIDOwner> findByCertFingerprint(String certFingerprint) {
        return where(Filter.equalTo("certFingerprint", certFingerprint));
    }

    public UUIDOwner findByUUID(String uuid) {
        return whereFindFirst(Filter.equalTo("uuid", uuid));
    }

    public void updateNodeType(String uuid, String nodeType) {
        update(Filter.equalTo("uuid", uuid), "nodeType", nodeType);
    }
}
