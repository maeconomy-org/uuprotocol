package io.recheck.uuidprotocol.domain.node.dto;

import io.recheck.uuidprotocol.domain.registrar.model.UUIDRegExp;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public abstract class NodeDTO<T> {

    @NotNull
    @Pattern(regexp = UUIDRegExp.re)
    private String uuid;

    public abstract T build();

}
