package io.recheck.uuidprotocol.nodenetwork.controller;


import io.recheck.uuidprotocol.common.security.X509UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class CommonController {

    @GetMapping({"/cert"})
    public ResponseEntity<Object> getCert(@AuthenticationPrincipal X509UserDetails user) {
        return ResponseEntity.ok(user);
    }

}
