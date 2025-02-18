package io.recheck.uuidprotocol.uuidservice;

import io.recheck.uuidprotocol.common.security.service.ClientUUIDService;
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

    private final ClientUUIDService clientUUIDService;

    @PostMapping("/uuid")
    public ResponseEntity<Object> createUUID(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(clientUUIDService.createUUID(user.getUsername()));
    }

    @GetMapping({"/uuid"})
    public ResponseEntity<Object> read() {
        return ResponseEntity.ok(clientUUIDService.findAllCastUUIDDTO());
    }

    @GetMapping({"/me/uuid"})
    public ResponseEntity<Object> readOwn(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(clientUUIDService.findByClientCastUUIDDTO(user.getUsername()));
    }

}
