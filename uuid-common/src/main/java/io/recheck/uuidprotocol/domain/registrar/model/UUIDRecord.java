package io.recheck.uuidprotocol.domain.registrar.model;

import io.recheck.uuidprotocol.common.firestore.model.FirestoreId;
import io.recheck.uuidprotocol.domain.audit.Audit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class UUIDRecord extends Audit {

    @FirestoreId
    private String uuid;

    private String ownerUuid;

    private UUIDRecordMeta uuidRecordMeta;

}
