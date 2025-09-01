package io.recheck.uuidprotocol.nodenetwork.aggregate;


import io.recheck.uuidprotocol.common.security.X509UserDetails;
import io.recheck.uuidprotocol.domain.aggregate.dto.AggregateFindDTO;
import io.recheck.uuidprotocol.domain.aggregate.dto.create.AggregateEntityCreateDTO;
import io.recheck.uuidprotocol.domain.aggregate.model.AggregateEntity;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.AggregateRepository;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/Aggregate")
public class AggregateController {

    private final AggregateRepository aggregateRepository;
    private final AggregateService aggregateService;

    @GetMapping({"/{uuid}"})
    public ResponseEntity<Object> findByAnyUUID(@PathVariable @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$") String uuid) {
        return ResponseEntity.ok(aggregateRepository.findByAnyUuid(uuid));
    }

    @GetMapping
    public Page<AggregateEntity> find(AggregateFindDTO aggregateFindDTO) {
        return aggregateRepository.find(aggregateFindDTO);
    }

    @PostMapping
    public ResponseEntity<AggregateEntity> create(@RequestBody AggregateEntityCreateDTO aggregateEntityCreateDTO, @AuthenticationPrincipal X509UserDetails user) {
        return ResponseEntity.ok(aggregateService.create(aggregateEntityCreateDTO, user.getCertificate().getCertificateSha256()));
    }

    @PostMapping({"/Import"})
    public ResponseEntity<Object> createMultiple(@RequestBody List<AggregateEntityCreateDTO> aggregateEntityList, @RequestHeader("CreatedBy") String createdBy) {
        aggregateService.createMultiple(aggregateEntityList, createdBy);
        return ResponseEntity.ok().build();
    }

}
