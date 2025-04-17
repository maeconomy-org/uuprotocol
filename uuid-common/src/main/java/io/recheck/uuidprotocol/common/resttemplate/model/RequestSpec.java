package io.recheck.uuidprotocol.common.resttemplate.model;

import lombok.Data;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Data
public class RequestSpec<REQ,RES> {

    private HttpMethod httpMethod;
    private String resourceAddress;
    private MultiValueMap<String, String> resourceQueryParams = new LinkedMultiValueMap<>();

    private REQ body;

    private Class<RES> responseBodyClass;
    private ParameterizedTypeReference<Iterable<RES>> responseBodyTypeReference;

    private int tryCounter = 3;
    private int tryMilliseconds = 8*1000;

}
