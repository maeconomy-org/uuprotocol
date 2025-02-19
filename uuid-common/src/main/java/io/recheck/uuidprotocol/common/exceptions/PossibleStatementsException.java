package io.recheck.uuidprotocol.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PossibleStatementsException extends ResponseStatusException {

    public PossibleStatementsException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

}
