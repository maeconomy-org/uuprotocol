package io.recheck.uuidprotocol.nodenetwork.node.controller;

import io.recheck.uuidprotocol.domain.node.dto.UUFileDTO;
import io.recheck.uuidprotocol.domain.node.model.UUFile;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRegExp;
import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;
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

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadFile(
            @RequestParam
            @Pattern(regexp = UUIDRegExp.re)
                    String uuidFile,
            @RequestParam
            @Pattern(regexp = UUIDRegExp.re)
                    String uuidToAttach,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetailsCustom user) {
        return ResponseEntity.ok(((UUFileNodeNetworkService) nodeNetworkService).storeOrReplaceFile(uuidToAttach, uuidFile, file, user));
    }

    @GetMapping("/download/{uuid}")
    public ResponseEntity<Resource> downloadFile(@AuthenticationPrincipal UserDetailsCustom user,
                                                    @PathVariable
                                                     @Pattern(regexp = UUIDRegExp.re)
                                                             String uuid) {
        return ((UUFileNodeNetworkService) nodeNetworkService).downloadFile(user, uuid);
    }

}
