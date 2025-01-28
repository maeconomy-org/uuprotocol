package io.recheck.uuidprotocol.domain.uuidowner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerUUID {

    private String uuid;

    private String ownerCertFingerprint;

}
