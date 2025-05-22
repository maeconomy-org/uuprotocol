package io.recheck.uuidprotocol.nodenetwork.aggregate;


import io.recheck.uuidprotocol.nodenetwork.aggregate.model.AggregateEntity;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public Page<AggregateEntity> findByLastUpdatedAtDeepest(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return aggregateService.findByLastUpdatedAtDeepest(page, size);
    }

}
