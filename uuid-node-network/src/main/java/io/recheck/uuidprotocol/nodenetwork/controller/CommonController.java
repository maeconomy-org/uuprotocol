package io.recheck.uuidprotocol.nodenetwork.controller;


import io.recheck.uuidprotocol.nodenetwork.common.ClassResolver;
import io.recheck.uuidprotocol.nodenetwork.datasource.NodeDataSource;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class CommonController {

    private final ClassResolver classResolver;

    @GetMapping({"/cert"})
    public ResponseEntity<Object> getCert(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }

    @GetMapping({"/{uuid}"})
    public ResponseEntity<Object> findByUUID(@PathVariable @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$") String uuid) {
        NodeDataSource<?> nodeDataSource = classResolver.getNodeDataSourceForUUID(uuid);
        return ResponseEntity.ok(nodeDataSource.findByUUID(uuid));
    }

}
