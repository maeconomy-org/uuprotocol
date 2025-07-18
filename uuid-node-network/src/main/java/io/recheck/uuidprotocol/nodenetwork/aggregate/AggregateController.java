package io.recheck.uuidprotocol.nodenetwork.aggregate;


import io.recheck.uuidprotocol.nodenetwork.aggregate.model.AggregateEntity;
import io.recheck.uuidprotocol.nodenetwork.aggregate.model.AggregateFindDTO;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/aggregate")
public class AggregateController {

    private final AggregateService aggregateService;

    @GetMapping({"/{uuid}"})
    public ResponseEntity<Object> findByAnyUUID(@PathVariable @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$") String uuid) {
        return ResponseEntity.ok(aggregateService.findByAnyUuid(uuid));
    }

    @GetMapping
    public Page<AggregateEntity> find(AggregateFindDTO aggregateFindDTO) {
        return aggregateService.find(aggregateFindDTO);
    }

}
