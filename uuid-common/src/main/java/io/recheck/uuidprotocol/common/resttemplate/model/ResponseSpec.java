package io.recheck.uuidprotocol.common.resttemplate.model;

import lombok.Data;
import org.springframework.http.HttpHeaders;

@Data
public class ResponseSpec<Res> {

    private HttpHeaders httpHeaders;

    private Res body;

    private String rawBody;

}
