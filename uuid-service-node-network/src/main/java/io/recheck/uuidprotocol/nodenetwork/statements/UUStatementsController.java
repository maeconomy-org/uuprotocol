package io.recheck.uuidprotocol.nodenetwork.statements;

import io.recheck.uuidprotocol.domain.statements.dto.UUStatementDTO;
import io.recheck.uuidprotocol.domain.statements.dto.UUStatementFindDTO;
import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;
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
    public ResponseEntity<Object> findOrCreate(@AuthenticationPrincipal UserDetailsCustom user,
                                               @Valid @RequestBody @NotEmpty List<UUStatementDTO> uuStatementDTOList) {
        return ResponseEntity.ok(uuStatementsService.findOrCreateWithOpposite(uuStatementDTOList, user));
    }

    @DeleteMapping
    public ResponseEntity<Object> softDelete(@AuthenticationPrincipal UserDetailsCustom user,
                                             @Valid @RequestBody UUStatementDTO uuStatementDTO) {
        return ResponseEntity.ok(uuStatementsService.softDeleteWithOpposite(uuStatementDTO, user));
    }

    @GetMapping
    public ResponseEntity<Object> findByDTOAndOrderByLastUpdatedAt(@AuthenticationPrincipal UserDetailsCustom user,
                                                                   @Valid UUStatementFindDTO uuStatementFindDTO) {
        return ResponseEntity.ok(uuStatementsDataSource.findByDTOAndOrderByLastUpdatedAt(user, uuStatementFindDTO));
    }

}
