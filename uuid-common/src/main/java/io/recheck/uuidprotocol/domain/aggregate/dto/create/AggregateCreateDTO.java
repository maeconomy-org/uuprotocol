package io.recheck.uuidprotocol.domain.aggregate.dto.create;

import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggregateCreateDTO {

    @NotEmpty
    private List<AggregateEntityCreateDTO> aggregateEntityList;

    @NotNull
    private UserDetailsCustom user;

}
