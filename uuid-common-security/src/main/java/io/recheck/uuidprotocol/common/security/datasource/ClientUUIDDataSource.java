package io.recheck.uuidprotocol.common.security.datasource;

import io.recheck.uuidprotocol.common.datasource.AbstractFirestoreDataSource;
import io.recheck.uuidprotocol.common.security.dto.UUIDDTO;
import io.recheck.uuidprotocol.common.security.model.ClientUUID;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientUUIDDataSource extends AbstractFirestoreDataSource<ClientUUID> {

    public ClientUUIDDataSource() {
        super(ClientUUID.class);
    }

    public List<ClientUUID> findByClientCertFingerprint(String clientCertFingerprint) {
        return whereEqualTo("clientCertFingerprint", clientCertFingerprint);
    }

    public List<UUIDDTO> findByClientCertFingerprintCastUUIDDTO(String clientCertFingerprint) {
        return whereEqualTo("clientCertFingerprint", clientCertFingerprint, UUIDDTO.class);
    }

    public ClientUUID findByUUID(String uuid) {
        List<ClientUUID> clientUUIDList = whereEqualTo("uuid", uuid);
        if (clientUUIDList.isEmpty()) {
            return null;
        }
        return clientUUIDList.get(0);
    }

    public List<ClientUUID> findByUUIDList(List<String> uuidList) {
        return whereIn("uuid", uuidList);
    }
}
