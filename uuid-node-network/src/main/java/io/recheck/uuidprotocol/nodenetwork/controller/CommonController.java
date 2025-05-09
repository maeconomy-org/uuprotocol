package io.recheck.uuidprotocol.nodenetwork.controller;


import io.recheck.uuidprotocol.domain.owner.model.UUIDOwner;
import io.recheck.uuidprotocol.nodenetwork.common.ServiceResolver;
import io.recheck.uuidprotocol.nodenetwork.datasource.NodeDataSource;
import io.recheck.uuidprotocol.nodenetwork.owner.UUIDOwnerService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class CommonController {

    private final UUIDOwnerService uuidOwnerService;
    private final ServiceResolver serviceResolver;

    @GetMapping({"/cert"})
    public ResponseEntity<Object> getCert(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }

    @GetMapping({"/{uuid}"})
    @SneakyThrows
    public ResponseEntity<Object> findByUUID(@PathVariable @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$") String uuid) {
        UUIDOwner uuidOwner = uuidOwnerService.findByUUID(uuid);
        if (!StringUtils.hasText(uuidOwner.getNodeType())) {
            return ResponseEntity.ok(uuidOwner);
        }
        Class<?> classType = Class.forName("io.recheck.uuidprotocol.domain.node.model."+uuidOwner.getNodeType());
        NodeDataSource<?> nodeDataSource = serviceResolver.getServiceForType(classType);
        return ResponseEntity.ok(nodeDataSource.findByUUID(uuid));
    }

}
