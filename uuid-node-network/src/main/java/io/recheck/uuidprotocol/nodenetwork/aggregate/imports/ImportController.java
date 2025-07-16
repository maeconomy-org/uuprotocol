package io.recheck.uuidprotocol.nodenetwork.aggregate.imports;

import io.recheck.uuidprotocol.nodenetwork.aggregate.model.AggregateEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/Import")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @PostMapping
    public ResponseEntity<Object> saveAll(  @RequestHeader("CreatedBy") String createdBy,
                                            @RequestBody List<AggregateEntity> aggregateEntityList) {
        importService.saveAll(aggregateEntityList, createdBy);
        return ResponseEntity.ok().build();
    }

}
