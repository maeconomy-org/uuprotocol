package io.recheck.uuidprotocol.nodenetwork.import_;

import io.recheck.uuidprotocol.common.security.X509UserDetails;
import io.recheck.uuidprotocol.nodenetwork.aggregate.model.AggregateEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/Import")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @PostMapping
    public ResponseEntity<Object> saveAll(@RequestBody List<AggregateEntity> aggregateEntityList, @AuthenticationPrincipal X509UserDetails user) {
        importService.saveAll(aggregateEntityList, user.getCertFingerprint());
        return ResponseEntity.ok().build();
    }

}
