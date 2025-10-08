package io.recheck.uuidprotocol.common.resttemplate;

import io.recheck.uuidprotocol.common.resttemplate.model.RequestSpec;
import io.recheck.uuidprotocol.common.resttemplate.model.ResponseSpec;
import io.recheck.uuidprotocol.common.resttemplate.model.ServerSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplateHandler;

import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.StandardCharsets;


@RequiredArgsConstructor
public class RestTemplateImpl {

    private final RestTemplate restTemplate;
    private final ServerSpec serverSpec;
    private UriTemplateHandler uriTemplateHandler = RestTemplateImpl.initUriTemplateHandler();

    public <REQ, RES> ResponseSpec send(RequestSpec<REQ,RES> requestSpec) {
        String uriString = UriComponentsBuilder
                .fromUriString(serverSpec.getBaseAddress() + requestSpec.getResourceAddress())
                .queryParams(requestSpec.getResourceQueryParams())
                .toUriString();

        RequestEntity.BodyBuilder requestBuilder = RequestEntity
                .method(requestSpec.getHttpMethod(), uriString)
                .acceptCharset(StandardCharsets.UTF_8);

        if (requestSpec.getHttpHeaders() != null) {
            requestBuilder.headers(requestSpec.getHttpHeaders());
        }


        RequestEntity requestEntity;
        if (requestSpec.getBody() != null) {
            requestEntity = requestBuilder.body(requestSpec.getBody());
        }
        else {
            requestEntity = requestBuilder.build();
        }


        ResponseEntity responseEntity = exchange(requestSpec, requestEntity);

        ResponseSpec responseSpec = new ResponseSpec<>();
        responseSpec.setBody(responseEntity.getBody());
        responseSpec.setHttpHeaders(responseEntity.getHeaders());
        return responseSpec;
    }


    public <REQ, RES> ResponseEntity<RES> exchange(RequestSpec<REQ,RES> requestSpec, RequestEntity requestEntity) {
        Type type = requestSpec.getResponseBodyClass();
        if (requestSpec.getResponseBodyTypeReference() != null) {
            type = requestSpec.getResponseBodyTypeReference().getType();
        }

        RequestCallback requestCallback = restTemplate.httpEntityCallback(requestEntity, type);
        ResponseExtractor<ResponseEntity<RES>> responseExtractor = restTemplate.responseEntityExtractor(type);
        return restTemplate.execute(resolveUrl(requestEntity), requestEntity.getMethod(), requestCallback, responseExtractor);
    }

    private URI resolveUrl(RequestEntity<?> entity) {
        if (entity instanceof RequestEntity.UriTemplateRequestEntity<?> ext) {
            if (ext.getVars() != null) {
                return this.uriTemplateHandler.expand(ext.getUriTemplate(), ext.getVars());
            }
            else if (ext.getVarsMap() != null) {
                return this.uriTemplateHandler.expand(ext.getUriTemplate(), ext.getVarsMap());
            }
            else {
                throw new IllegalStateException("No variables specified for URI template: " + ext.getUriTemplate());
            }
        }
        else {
            return entity.getUrl();
        }
    }

    private static DefaultUriBuilderFactory initUriTemplateHandler() {
        DefaultUriBuilderFactory uriFactory = new DefaultUriBuilderFactory();
        uriFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT);  // for backwards compatibility..
        return uriFactory;
    }

}