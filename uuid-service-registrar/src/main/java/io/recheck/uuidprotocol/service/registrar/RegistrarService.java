package io.recheck.uuidprotocol.service.registrar;

import io.recheck.uuidprotocol.common.exceptions.ForbiddenException;
import io.recheck.uuidprotocol.common.exceptions.NotFoundException;
import io.recheck.uuidprotocol.domain.registrar.dto.UUIDRecordAuthorizePostRequestDTO;
import io.recheck.uuidprotocol.domain.registrar.dto.UUIDRecordAuthorizePostResponseDTO;
import io.recheck.uuidprotocol.domain.registrar.dto.UUIDRecordMetaPutRequestDTO;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRecord;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRecordMeta;
import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrarService {

    private final RegistrarDataSource registrarDataSource;

    public UUIDRecord create(UserDetailsCustom user) {
        String uuid = UUID.randomUUID().toString();
        UUIDRecord uuidRecord = new UUIDRecord(uuid, user.getUserUUID(), null);
        return registrarDataSource.createAudit(uuidRecord, user);
    }

    public UUIDRecordAuthorizePostResponseDTO authorize(UUIDRecordAuthorizePostRequestDTO dto) {
        UUIDRecord existingUUIDRecord = registrarDataSource.findByUuid(dto.getResourceId());
        if (existingUUIDRecord == null) {
            throw new NotFoundException("UUID not found");
        }
        if (!existingUUIDRecord.getOwnerUUID().equals(dto.getUserUuid())) {
            throw new ForbiddenException("The UUID does not belong to this user");
        }
        return new UUIDRecordAuthorizePostResponseDTO(true);
    }

    public UUIDRecord updateUUIDRecordMeta(UUIDRecordMetaPutRequestDTO dto) {
        UUIDRecord uuidRecord = registrarDataSource.findByUuid(dto.getUuid());
        uuidRecord.setUuidRecordMeta(new UUIDRecordMeta(dto.getNodeType()));
        return registrarDataSource.createOrUpdate(uuidRecord);
    }

}
