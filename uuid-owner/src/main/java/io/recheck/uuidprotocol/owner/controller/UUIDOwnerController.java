package io.recheck.uuidprotocol.owner.controller;

import io.recheck.uuidprotocol.domain.uuidowner.UUIDOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/UUIDOwner")
@RequiredArgsConstructor
public class UUIDOwnerController {

    private final UUIDOwnerService uuidOwnerService;

    @PostMapping
    public ResponseEntity<Object> createUUID(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(uuidOwnerService.createUUID(user.getUsername()));
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return ResponseEntity.ok(uuidOwnerService.findAll());
    }

    @GetMapping({"/own"})
    public ResponseEntity<Object> findByOwner(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(uuidOwnerService.findByOwner(user.getUsername()));
    }

}
