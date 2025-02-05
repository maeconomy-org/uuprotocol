package io.recheck.uuidprotocol.domain.uuidowner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.recheck.uuidprotocol.common.datasource.model.FirestoreId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerUUID {

    @FirestoreId
    @JsonIgnore
    private String id;

    private String uuid;

    @JsonIgnore
    private String ownerCertFingerprint;

    private String nodeType;

    public OwnerUUID(String uuid, String ownerCertFingerprint) {
        this.uuid = uuid;
        this.ownerCertFingerprint = ownerCertFingerprint;
    }
}
