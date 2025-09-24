package io.recheck.uuidprotocol.nodenetwork.controller;


import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class UserController {

    @GetMapping({"/User"})
    public ResponseEntity<Object> getCert(@AuthenticationPrincipal UserDetailsCustom user) {
        return ResponseEntity.ok(user);
    }

}
