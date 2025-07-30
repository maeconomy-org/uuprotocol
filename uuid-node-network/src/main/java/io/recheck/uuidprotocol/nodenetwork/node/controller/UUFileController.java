package io.recheck.uuidprotocol.nodenetwork.node.controller;

import io.recheck.uuidprotocol.common.security.X509UserDetails;
import io.recheck.uuidprotocol.domain.node.dto.UUFileDTO;
import io.recheck.uuidprotocol.domain.node.model.UUFile;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.UUFileDataSource;
import io.recheck.uuidprotocol.nodenetwork.node.service.UUFileNodeNetworkService;
import jakarta.validation.constraints.Pattern;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/UUFile")
public class UUFileController extends NodeController<UUFile, UUFileDTO> {
    public UUFileController(UUFileNodeNetworkService uuFileNodeNetworkService, UUFileDataSource uuFileDataSource) {
        super(uuFileNodeNetworkService, uuFileDataSource);
    }

    @PostMapping("/upload/{uuid}")
    public ResponseEntity<Object> uploadFile(
            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
                    String uuid,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal X509UserDetails user) {
        return ResponseEntity.ok(((UUFileNodeNetworkService) nodeNetworkService).storeOrReplaceFile(uuid, file, user));
    }

    @GetMapping("/download/{uuid}")
    public ResponseEntity<Resource> downloadFile(@AuthenticationPrincipal X509UserDetails user,
                                                    @PathVariable
                                                     @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
                                                             String uuid) {
        return ((UUFileNodeNetworkService) nodeNetworkService).downloadFile(user, uuid);
    }

}
