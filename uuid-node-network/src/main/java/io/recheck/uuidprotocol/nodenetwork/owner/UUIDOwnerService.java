package io.recheck.uuidprotocol.nodenetwork.owner;

import io.recheck.uuidprotocol.common.resttemplate.RestTemplateImpl;
import io.recheck.uuidprotocol.common.resttemplate.model.RequestSpec;
import io.recheck.uuidprotocol.common.resttemplate.model.ResponseSpec;
import io.recheck.uuidprotocol.domain.owner.model.UUIDOwner;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UUIDOwnerService {

    private final RestTemplateImpl restTemplateImpl;

    public UUIDOwner postUUID() {
        RequestSpec<?, UUIDOwner> requestSpec = new RequestSpec<>();
        requestSpec.setHttpMethod(HttpMethod.POST);
        requestSpec.setResourceAddress("/api/UUIDOwner");
        requestSpec.setResponseBodyClass(UUIDOwner.class);

        ResponseSpec<UUIDOwner> responseSpec = restTemplateImpl.send(requestSpec);
        return responseSpec.getBody();
    }

    public List<UUIDOwner> getOwnUUID() {
        RequestSpec<?, UUIDOwner> requestSpec = new RequestSpec<>();
        requestSpec.setHttpMethod(HttpMethod.GET);
        requestSpec.setResourceAddress("/api/UUIDOwner/own");
        requestSpec.setResponseBodyTypeReference(new ParameterizedTypeReference<>() {});

        ResponseSpec<List<UUIDOwner>> responseSpec = restTemplateImpl.send(requestSpec);
        return responseSpec.getBody();
    }

    public UUIDOwner findByUUID(String uuid) {
        RequestSpec<?, UUIDOwner> requestSpec = new RequestSpec<>();
        requestSpec.setHttpMethod(HttpMethod.GET);
        requestSpec.setResourceAddress("/api/UUIDOwner/"+uuid);
        requestSpec.setResponseBodyClass(UUIDOwner.class);

        ResponseSpec<UUIDOwner> responseSpec = restTemplateImpl.send(requestSpec);
        return responseSpec.getBody();
    }

    public List<UUIDOwner> getAllUUID() {
        RequestSpec<?, UUIDOwner> requestSpec = new RequestSpec<>();
        requestSpec.setHttpMethod(HttpMethod.GET);
        requestSpec.setResourceAddress("/api/UUIDOwner");
        requestSpec.setResponseBodyTypeReference(new ParameterizedTypeReference<>() {});

        ResponseSpec<List<UUIDOwner>> responseSpec = restTemplateImpl.send(requestSpec);
        return responseSpec.getBody();
    }

    public UUIDOwner validateOwnerUUID(String certFingerprint, String uuid) {
        RequestSpec<?, UUIDOwner> requestSpec = new RequestSpec<>();
        requestSpec.setHttpMethod(HttpMethod.GET);
        requestSpec.setResourceAddress("/api/UUIDOwner/validateOwnerUUID");
        requestSpec.getResourceQueryParams().put("certFingerprint", List.of(certFingerprint));
        requestSpec.getResourceQueryParams().put("uuid", List.of(uuid));
        requestSpec.setResponseBodyClass(UUIDOwner.class);

        ResponseSpec<UUIDOwner> responseSpec = restTemplateImpl.send(requestSpec);
        return responseSpec.getBody();
    }

    public UUIDOwner updateNodeType(String uuid, String nodeType) {
        RequestSpec<?, UUIDOwner> requestSpec = new RequestSpec<>();
        requestSpec.setHttpMethod(HttpMethod.POST);
        requestSpec.setResourceAddress("/api/UUIDOwner/updateNodeType");
        requestSpec.getResourceQueryParams().put("uuid", List.of(uuid));
        requestSpec.getResourceQueryParams().put("nodeType", List.of(nodeType));
        requestSpec.setResponseBodyClass(UUIDOwner.class);

        ResponseSpec<UUIDOwner> responseSpec = restTemplateImpl.send(requestSpec);
        return responseSpec.getBody();
    }

}
