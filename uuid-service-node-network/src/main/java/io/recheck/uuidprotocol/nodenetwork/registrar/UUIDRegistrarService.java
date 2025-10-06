package io.recheck.uuidprotocol.nodenetwork.registrar;

import io.recheck.uuidprotocol.common.resttemplate.RestTemplateImpl;
import io.recheck.uuidprotocol.common.resttemplate.model.RequestSpec;
import io.recheck.uuidprotocol.common.resttemplate.model.ResponseSpec;
import io.recheck.uuidprotocol.domain.registrar.dto.UUIDRecordAuthorizePostRequestDTO;
import io.recheck.uuidprotocol.domain.registrar.dto.UUIDRecordAuthorizePostResponseDTO;
import io.recheck.uuidprotocol.domain.registrar.dto.UUIDRecordMetaPutRequestDTO;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRecord;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRecordMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UUIDRegistrarService {

    private final RestTemplateImpl restTemplateImplUUIDRegistrar;

    public UUIDRecord findByUUID(String uuid) {
        RequestSpec<?, UUIDRecord> requestSpec = new RequestSpec<>();
        requestSpec.setHttpMethod(HttpMethod.GET);
        requestSpec.setResourceAddress("/api/UUID/"+uuid);
        requestSpec.setResponseBodyClass(UUIDRecord.class);

        ResponseSpec<UUIDRecord> responseSpec = restTemplateImplUUIDRegistrar.send(requestSpec);
        return responseSpec.getBody();
    }

    public UUIDRecordAuthorizePostResponseDTO authorize(String userUuid, String resourceId) {
        RequestSpec<UUIDRecordAuthorizePostRequestDTO, UUIDRecordAuthorizePostResponseDTO> requestSpec = new RequestSpec<>();
        requestSpec.setHttpMethod(HttpMethod.POST);
        requestSpec.setResourceAddress("/api/UUID/authorize");

        requestSpec.setBody(new UUIDRecordAuthorizePostRequestDTO(userUuid, resourceId));

        requestSpec.setResponseBodyClass(UUIDRecordAuthorizePostResponseDTO.class);
        ResponseSpec<UUIDRecordAuthorizePostResponseDTO> responseSpec = restTemplateImplUUIDRegistrar.send(requestSpec);
        return responseSpec.getBody();
    }

    public UUIDRecord updateUUIDRecordMeta(String uuid, UUIDRecordMeta uuidRecordMeta) {
        RequestSpec<UUIDRecordMetaPutRequestDTO, UUIDRecord> requestSpec = new RequestSpec<>();
        requestSpec.setHttpMethod(HttpMethod.PUT);
        requestSpec.setResourceAddress("/api/UUID/UUIDRecordMeta");

        requestSpec.setBody(new UUIDRecordMetaPutRequestDTO(uuid, uuidRecordMeta));

        requestSpec.setResponseBodyClass(UUIDRecord.class);
        ResponseSpec<UUIDRecord> responseSpec = restTemplateImplUUIDRegistrar.send(requestSpec);
        return responseSpec.getBody();
    }

}
