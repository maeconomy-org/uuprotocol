package io.recheck.uuidprotocol.nodenetwork.aggregate.imports;

import io.recheck.uuidprotocol.common.firestore.FirestoreDataSource;
import io.recheck.uuidprotocol.domain.owner.model.UUIDOwner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UUIDOwnerImportDataSource extends FirestoreDataSource<UUIDOwner> {

    public UUIDOwnerImportDataSource() {
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
