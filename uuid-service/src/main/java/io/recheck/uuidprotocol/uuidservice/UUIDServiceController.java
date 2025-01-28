package io.recheck.uuidprotocol.uuidservice;

import io.recheck.uuidprotocol.domain.uuidowner.OwnerUUIDService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UUIDServiceController {

    private final OwnerUUIDService ownerUUIDService;

    @PostMapping("/uuid")
    public ResponseEntity<Object> createUUID(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(ownerUUIDService.createUUID(user.getUsername()));
    }

    @GetMapping({"/uuid"})
    public ResponseEntity<Object> read() {
        return ResponseEntity.ok(ownerUUIDService.findAllCastUUIDDTO());
    }

    @GetMapping({"/me/uuid"})
    public ResponseEntity<Object> readOwn(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(ownerUUIDService.findByOwnerCastUUIDDTO(user.getUsername()));
    }

}
