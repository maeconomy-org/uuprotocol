package io.recheck.uuidprotocol.common.exceptions;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ProblemDetail;

import java.util.Map;

@Data
@AllArgsConstructor
class ProblemDetailEx {

    @JsonUnwrapped
    private ProblemDetail problemDetail;

    private Map<String, String> errors;
}
