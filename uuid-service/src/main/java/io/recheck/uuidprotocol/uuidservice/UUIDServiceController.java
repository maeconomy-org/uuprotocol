package io.recheck.uuidprotocol.uuidservice;

import io.recheck.uuidprotocol.domain.uuidowner.OwnerUUIDService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/OwnerUUID")
@RequiredArgsConstructor
public class UUIDServiceController {

    private final OwnerUUIDService ownerUUIDService;

    @PostMapping
    public ResponseEntity<Object> createUUID(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(ownerUUIDService.createUUID(user.getUsername()));
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(ownerUUIDService.findAll());
    }

    @GetMapping({"/own"})
    public ResponseEntity<Object> findByOwner(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(ownerUUIDService.findByOwner(user.getUsername()));
    }

}
