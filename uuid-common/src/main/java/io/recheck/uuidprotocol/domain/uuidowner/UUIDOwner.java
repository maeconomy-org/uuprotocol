package io.recheck.uuidprotocol.domain.uuidowner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.recheck.uuidprotocol.common.datasource.model.FirestoreId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UUIDOwner {

    @FirestoreId
    @JsonIgnore
    private String id;

    private String uuid;

    @JsonIgnore
    private String certFingerprint;

    private String nodeType;

    public UUIDOwner(String uuid, String certFingerprint) {
        this.uuid = uuid;
        this.certFingerprint = certFingerprint;
    }
}
