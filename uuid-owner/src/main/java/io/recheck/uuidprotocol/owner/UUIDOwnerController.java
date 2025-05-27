package io.recheck.uuidprotocol.owner;

import io.recheck.uuidprotocol.common.security.X509UserDetails;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/UUIDOwner")
@RequiredArgsConstructor
public class UUIDOwnerController {

    private final UUIDOwnerService uuidOwnerService;

    @PostMapping
    public ResponseEntity<Object> createUUID(@AuthenticationPrincipal X509UserDetails user) {
        return ResponseEntity.ok(uuidOwnerService.createUUID(user.getCertFingerprint()));
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(uuidOwnerService.findAll());
    }

    @GetMapping({"/own"})
    public ResponseEntity<Object> findByOwner(@AuthenticationPrincipal X509UserDetails user) {
        return ResponseEntity.ok(uuidOwnerService.findByOwner(user.getCertFingerprint()));
    }

    @GetMapping({"/{uuid}"})
    public ResponseEntity<Object> findByUUID(@PathVariable @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$") String uuid) {
        return ResponseEntity.ok(uuidOwnerService.findByUUID(uuid));
    }

    @GetMapping({"/validateOwnerUUID"})
    public ResponseEntity<Object> validateOwnerUUID(@RequestParam
                                                        String certFingerprint,
                                                    @RequestParam
                                                    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
                                                        String uuid) {
        return ResponseEntity.ok(uuidOwnerService.validateOwnerUUID(certFingerprint, uuid));
    }

    @PostMapping ({"/updateNodeType"})
    ResponseEntity<Object> updateNodeType(@RequestParam
                                                       @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
                                                               String uuid,
                                                       @RequestParam
                                                            String nodeType) {
        return ResponseEntity.ok(uuidOwnerService.updateNodeType(uuid, nodeType));
    }

}
