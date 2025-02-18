package io.recheck.uuidprotocol.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class QueryBuildException extends ResponseStatusException {

    public QueryBuildException(String message){
        super(HttpStatus.BAD_REQUEST, message);
    }

}
