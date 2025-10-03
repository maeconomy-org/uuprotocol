package io.recheck.uuidprotocol.service.registrar;

import io.recheck.uuidprotocol.domain.registrar.dto.UUIDRecordAuthorizePostRequestDTO;
import io.recheck.uuidprotocol.domain.registrar.dto.UUIDRecordMetaPutRequestDTO;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRegExp;
import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/UUID")
@RequiredArgsConstructor
public class RegistrarController {

    private final RegistrarService registrarService;
    private final RegistrarDataSource registrarDataSource;

    @PostMapping
    public ResponseEntity<Object> create(@AuthenticationPrincipal UserDetailsCustom user) {
        return ResponseEntity.ok(registrarService.create(user));
    }

    @GetMapping({"/own"})
    public ResponseEntity<Object> findByOwnerUuid(@AuthenticationPrincipal UserDetailsCustom user) {
        return ResponseEntity.ok(registrarDataSource.findByOwnerUUID(user.getUserUUID()));
    }

    @GetMapping({"/{uuid}"})
    public ResponseEntity<Object> findByUuid(@PathVariable @Pattern(regexp = UUIDRegExp.re) String uuid) {
        return ResponseEntity.ok(registrarDataSource.findByUuid(uuid));
    }

    @PostMapping({"/authorize"})
    public ResponseEntity<Object> authorize(@Valid @RequestBody UUIDRecordAuthorizePostRequestDTO dto) {
        return ResponseEntity.ok(registrarService.authorize(dto));
    }

    @PutMapping({"/UUIDRecordMeta"})
    public ResponseEntity<Object> updateUUIDRecordMeta(@Valid @RequestBody UUIDRecordMetaPutRequestDTO dto) {
        return ResponseEntity.ok(registrarService.updateUUIDRecordMeta(dto));
    }

}
