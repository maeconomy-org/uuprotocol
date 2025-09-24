package io.recheck.uuidprotocol.nodenetwork.aggregate;


import io.recheck.uuidprotocol.domain.aggregate.dto.create.AggregateCreateDTO;
import io.recheck.uuidprotocol.domain.audit.AuditUser;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRegExp;
import io.recheck.uuidprotocol.domain.aggregate.dto.AggregateFindDTO;
import io.recheck.uuidprotocol.domain.aggregate.model.AggregateEntity;
import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.AggregateRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/Aggregate")
public class AggregateController {

    private final AggregateRepository aggregateRepository;
    private final AggregateService aggregateService;

    @GetMapping({"/{uuid}"})
    public ResponseEntity<Object> findByAnyUUID(@PathVariable @Pattern(regexp = UUIDRegExp.re) String uuid) {
        return ResponseEntity.ok(aggregateRepository.findByAnyUuid(uuid));
    }

    @GetMapping
    public Page<AggregateEntity> find(@AuthenticationPrincipal UserDetailsCustom user, AggregateFindDTO aggregateFindDTO) {
        aggregateFindDTO.setCreatedBy(new AuditUser(user));
        return aggregateRepository.find(aggregateFindDTO);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody AggregateCreateDTO aggregateCreateDTO) {
        return ResponseEntity.ok(aggregateService.create(aggregateCreateDTO));
    }

    @PostMapping({"/Import"})
    public ResponseEntity<Object> createMultiple(@Valid @RequestBody AggregateCreateDTO aggregateCreateDTO) {
        aggregateService.createMultiple(aggregateCreateDTO);
        return ResponseEntity.ok().build();
    }

}
