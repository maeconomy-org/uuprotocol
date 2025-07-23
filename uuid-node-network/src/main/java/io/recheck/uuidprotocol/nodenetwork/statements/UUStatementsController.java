package io.recheck.uuidprotocol.nodenetwork.statements;

import io.recheck.uuidprotocol.common.security.X509UserDetails;
import io.recheck.uuidprotocol.domain.node.dto.UUStatementDTO;
import io.recheck.uuidprotocol.domain.node.dto.UUStatementFindDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/UUStatements")
@RequiredArgsConstructor
public class UUStatementsController {

    private final UUStatementsService uuStatementsService;
    private final UUStatementsDataSource uuStatementsDataSource;

    @PostMapping
    public ResponseEntity<Object> findOrCreate(@Valid @RequestBody @NotEmpty List<UUStatementDTO> uuStatementDTOList, @AuthenticationPrincipal X509UserDetails user) {
        return ResponseEntity.ok(uuStatementsService.findOrCreateWithOpposite(uuStatementDTOList, user.getCertFingerprint()));
    }

    @DeleteMapping
    public ResponseEntity<Object> softDelete(@Valid @RequestBody UUStatementDTO uuStatementDTO, @AuthenticationPrincipal X509UserDetails user) {
        return ResponseEntity.ok(uuStatementsService.softDeleteWithOpposite(uuStatementDTO, user.getCertFingerprint()));
    }

    @GetMapping
    public ResponseEntity<Object> findByDTOAndOrderByLastUpdatedAt(@Valid UUStatementFindDTO uuStatementFindDTO) {
        return ResponseEntity.ok(uuStatementsDataSource.findByDTOAndOrderByLastUpdatedAt(uuStatementFindDTO));
    }

}
