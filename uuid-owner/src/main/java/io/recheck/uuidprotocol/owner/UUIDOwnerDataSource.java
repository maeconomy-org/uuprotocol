package io.recheck.uuidprotocol.owner;

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
        return whereEqualTo("certFingerprint", certFingerprint);
    }

    public UUIDOwner findByUUID(String uuid) {
        List<UUIDOwner> UUIDOwnerList = whereEqualTo("uuid", uuid);
        if (UUIDOwnerList.isEmpty()) {
            return null;
        }
        return UUIDOwnerList.get(0);
    }
}
