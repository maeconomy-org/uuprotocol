package io.recheck.uuidprotocol.nodenetwork.controller;

import io.recheck.uuidprotocol.nodenetwork.datasource.UUStatementsDataSource;
import io.recheck.uuidprotocol.nodenetwork.dto.UUStatementDTO;
import io.recheck.uuidprotocol.nodenetwork.model.UUStatementPredicate;
import io.recheck.uuidprotocol.nodenetwork.service.UUStatementsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/UUStatements")
@RequiredArgsConstructor
public class UUStatementsController {

    private final UUStatementsService uuStatementsService;
    private final UUStatementsDataSource uuStatementsDataSource;

    @PostMapping
    public ResponseEntity<Object> createOrUpdateStatements(@Valid @RequestBody @NotEmpty List<UUStatementDTO> uuStatementDTOList, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(uuStatementsService.create(uuStatementDTOList, user.getUsername()));
    }

    @DeleteMapping({"/{subject}/{predicate}/{object}"})
    public ResponseEntity<Object> deleteUUStatements(@PathVariable
                                                         @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
                                                                 String subject,
                                                     @PathVariable UUStatementPredicate predicate,
                                                     @PathVariable
                                                         @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
                                                                 String object,
                                                     Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(uuStatementsService.softDelete(new UUStatementDTO(subject, predicate, object), user.getUsername()));
    }

    @GetMapping
    public ResponseEntity<Object> readAll() {
        return ResponseEntity.ok(uuStatementsDataSource.findAll());
    }

    @GetMapping({"/{predicate}"})
    public ResponseEntity<Object> readStatementsByPredicate(@PathVariable UUStatementPredicate predicate) {
        return ResponseEntity.ok(uuStatementsDataSource.findByPredicate(predicate.name()));
    }

    @GetMapping({"/{uuid}/{predicate}"})
    public ResponseEntity<Object> readStatementsByUUIDAndPredicate(@PathVariable
                                                       @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
                                                               String uuid,
                                                       @PathVariable UUStatementPredicate predicate) {
        return ResponseEntity.ok(uuStatementsDataSource.findBySubjectAndPredicate(uuid, predicate.name()));
    }

}
