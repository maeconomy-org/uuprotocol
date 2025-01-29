package io.recheck.uuidprotocol.domain.uuidowner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerUUID {

    private String uuid;

    @JsonIgnore
    private String ownerCertFingerprint;

    public OwnerUUID(String uuid) {
        this.uuid = uuid;
    }
}
