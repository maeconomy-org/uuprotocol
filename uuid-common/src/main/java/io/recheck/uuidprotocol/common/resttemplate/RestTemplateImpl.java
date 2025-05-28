package io.recheck.uuidprotocol.common.resttemplate;

import io.recheck.uuidprotocol.common.resttemplate.model.RequestSpec;
import io.recheck.uuidprotocol.common.resttemplate.model.ResponseSpec;
import io.recheck.uuidprotocol.common.resttemplate.model.ServerSpec;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;


@RequiredArgsConstructor
public class RestTemplateImpl {

    private final RestTemplate restTemplate;
    private final ServerSpec serverSpec;

    @SneakyThrows
    public <REQ, RES> ResponseSpec send(RequestSpec<REQ,RES> requestSpec) {
        String uriString = UriComponentsBuilder
                .fromUriString(serverSpec.getBaseAddress() + requestSpec.getResourceAddress())
                .queryParams(requestSpec.getResourceQueryParams())
                .toUriString();

        RequestEntity.BodyBuilder requestBuilder = RequestEntity
                .method(requestSpec.getHttpMethod(), uriString)
                .acceptCharset(StandardCharsets.UTF_8);

        RequestEntity requestEntity;
        if (requestSpec.getBody() != null) {
            requestEntity = requestBuilder.body(requestSpec.getBody());
        }
        else {
            requestEntity = requestBuilder.build();
        }


        if (requestSpec.getResponseBodyTypeReference() != null) {
            ResponseEntity<Iterable<RES>> responseEntity = restTemplate.exchange(requestEntity, requestSpec.getResponseBodyTypeReference());

            ResponseSpec<Iterable<RES>> responseSpec = new ResponseSpec<>();
            responseSpec.setBody(responseEntity.getBody());
            responseSpec.setHttpHeaders(responseEntity.getHeaders());
            return responseSpec;
        }
        else {
            ResponseEntity<RES> responseEntity = restTemplate.exchange(requestEntity, requestSpec.getResponseBodyClass());

            ResponseSpec<RES> responseSpec = new ResponseSpec<>();
            responseSpec.setBody(responseEntity.getBody());
            responseSpec.setHttpHeaders(responseEntity.getHeaders());
            return responseSpec;
        }

    }

}
