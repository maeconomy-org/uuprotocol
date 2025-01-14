package io.recheck.uuidprotocol.nodenetwork;

import io.recheck.uuidprotocol.common.security.model.ClientUUID;
import io.recheck.uuidprotocol.common.security.service.ClientUUIDService;
import io.recheck.uuidprotocol.common.utils.ListUtils;
import io.recheck.uuidprotocol.nodenetwork.dto.UUObjectDTO;
import io.recheck.uuidprotocol.nodenetwork.dto.UUPropertyDTO;
import io.recheck.uuidprotocol.nodenetwork.dto.UUPropertyValueDTO;
import io.recheck.uuidprotocol.nodenetwork.dto.UUStatementsDTO;
import io.recheck.uuidprotocol.nodenetwork.model.UUStatements;
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
@RequiredArgsConstructor
public class UUNodeNetworkController {

    private final UUIDNodeNetworkService uuidNodeNetworkService;
    private final ClientUUIDService clientUUIDService;


    /*
    ================ UUObject ======================
     */
    @GetMapping({"/UUObject"})
    public ResponseEntity<Object> listUUObject() {
        return ResponseEntity.ok(uuidNodeNetworkService.findUUObjectAll());
    }

    @GetMapping({"/me/UUObject"})
    public ResponseEntity<Object> readUUObjectOwn(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<ClientUUID> clientUUIDS = clientUUIDService.findByClient(user.getUsername());
        List<String> uuidList = clientUUIDS.stream()
                .map(ClientUUID::getUuid)
                .toList();

        return ResponseEntity.ok(uuidNodeNetworkService.findUUObjectByUUID(uuidList));
    }

    @GetMapping({"/UUObject/{uuid}"})
    public ResponseEntity<Object> readUUObject(@PathVariable
                                               @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
                                                       String uuid) {
        return ResponseEntity.ok(uuidNodeNetworkService.findUUObjectByUUID(uuid));
    }

    @PostMapping({"/UUObject"})
    public ResponseEntity<Object> createOrUpdateUUObject(@Valid @RequestBody UUObjectDTO data,
                                               Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        clientUUIDService.validateClientUUID(user.getUsername(), data.getUuid());
        return ResponseEntity.ok(uuidNodeNetworkService.createOrUpdateUUObject(data, user.getUsername()));
    }

    @DeleteMapping({"/UUObject/{uuid}"})
    public ResponseEntity<Object> deleteUUObject(@PathVariable
                                                     @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
                                                             String uuid,
                                                 Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        clientUUIDService.validateClientUUID(user.getUsername(), uuid);
        return ResponseEntity.ok(uuidNodeNetworkService.softDeleteUUObject(uuid, user.getUsername()));
    }

    /*
    ================ UUProperty ======================
     */

    @PostMapping({"/Property"})
    public ResponseEntity<Object> createOrUpdateUUProperty(@Valid @RequestBody UUPropertyDTO data,
                                                         Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        clientUUIDService.validateClientUUID(user.getUsername(), data.getUuid());
        uuidNodeNetworkService.validateUUPropertyUUID(data.getId(), data.getUuid());

        return ResponseEntity.ok(uuidNodeNetworkService.createOrUpdateUUProperty(data, user.getUsername()));
    }

    @GetMapping({"/Property"})
    public ResponseEntity<Object> readUUPropertyByUUID(@RequestParam
                                                 @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
                                                 String uuid) {
        return ResponseEntity.ok(uuidNodeNetworkService.findUUPropertyByUUID(uuid));
    }

    @GetMapping({"/Property/{propertyId}"})
    public ResponseEntity<Object> readUUPropertyById(@PathVariable String propertyId) {
        return ResponseEntity.ok(uuidNodeNetworkService.findUUPropertyById(propertyId));
    }

    @DeleteMapping({"/Property/{propertyId}"})
    public ResponseEntity<Object> deleteUUProperty(@PathVariable String propertyId,
                                                   Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        clientUUIDService.validateClientUUID(user.getUsername(), uuidNodeNetworkService.findUUPropertyById(propertyId).getUuid());
        return ResponseEntity.ok(uuidNodeNetworkService.softDeleteUUProperty(propertyId, user.getUsername()));
    }
    /*
    ================ UUPropertyValue ======================
     */

    @PostMapping({"/Property/value"})
    public ResponseEntity<Object> createOrUpdateUUPropertyValue(@Valid @RequestBody UUPropertyValueDTO data,
                                                           Authentication authentication) {
        uuidNodeNetworkService.validateUUPropertyValuePropertyId(data.getId(), data.getPropertyId());

        User user = (User) authentication.getPrincipal();
        clientUUIDService.validateClientUUID(user.getUsername(), uuidNodeNetworkService.findUUPropertyById(data.getPropertyId()).getUuid());

        return ResponseEntity.ok(uuidNodeNetworkService.createOrUpdateUUPropertyValue(data, user.getUsername()));
    }

    @GetMapping({"/Property/value"})
    public ResponseEntity<Object> readUUPropertyValue(@RequestParam String propertyId) {
        return ResponseEntity.ok(uuidNodeNetworkService.findUUPropertyValueByPropertyId(propertyId));
    }

    @GetMapping({"/Property/value/{valueId}"})
    public ResponseEntity<Object> readUUPropertyValueById(@PathVariable String valueId) {
        return ResponseEntity.ok(uuidNodeNetworkService.findUUPropertyValueById(valueId));
    }

    @DeleteMapping({"/Property/value/{valueId}"})
    public ResponseEntity<Object> deleteUUPropertyValue(@PathVariable String valueId,
                                                   Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        clientUUIDService.validateClientUUID(user.getUsername(), uuidNodeNetworkService.findUUPropertyById(
                                                                            uuidNodeNetworkService.findUUPropertyValueById(valueId).getPropertyId()).getUuid());
        return ResponseEntity.ok(uuidNodeNetworkService.softDeleteUUPropertyValue(valueId, user.getUsername()));
    }

//    @SneakyThrows
//    @GetMapping({"/search/Property"})
//    public ResponseEntity<Object> searchForProperties(@Valid @RequestBody UUPropertyRequestParamsDTO uuPropertyRequestParamsDTO) {
//        return ResponseEntity.ok(uuidNodeNetworkService.findUUPropertyByParams(uuPropertyRequestParamsDTO));
//    }

    /*
    ================ Statements ======================
     */
    @PostMapping({"/Statements"})
    public ResponseEntity<Object> createOrUpdateStatements(@Valid @RequestBody @NotEmpty List<UUStatementsDTO> uuStatementsDTOList, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        List<String> uuidObject1List = uuStatementsDTOList.stream()
                .map(UUStatementsDTO::getSubject)
                .toList();

        List<String> uuidObject2List = uuStatementsDTOList.stream()
                .map(UUStatementsDTO::getObject)
                .toList();

        clientUUIDService.validateClientUUID(user.getUsername(), ListUtils.concat(uuidObject1List, uuidObject2List));

        return ResponseEntity.ok(uuidNodeNetworkService.createOrUpdateUUStatements(uuStatementsDTOList, user.getUsername()));
    }

    @GetMapping({"/Statements"})
    public ResponseEntity<Object> readStatements() {
        return ResponseEntity.ok(uuidNodeNetworkService.findStatementsAll());
    }

    /*
    predicate = {
            "isParentOf", "isChildOf" Parent Object <-> Child Object
            "isInputOf", "isOutputOf" Input Object <-> Output Object
            "isModelOf", "isInstanceModelOf" Model <-> Object
            "isParentModelOf", "isChildInstanceModelOf" Parent Model <-> Child Model
            "isInputModelOf", "isOutputModelOf" Input Model <-> Output Model
        }
     */
    @GetMapping({"/Statements/{uuid}/{predicate}"})
    public ResponseEntity<Object> readStatementsByUUID(@PathVariable
                                                 @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
                                                 String uuid,
                                                       @PathVariable String predicate) {
        return ResponseEntity.ok(uuidNodeNetworkService.findStatementsBySubjectAndPredicate(uuid, predicate));
    }

    @DeleteMapping({"/Statements/{statementsId}"})
    public ResponseEntity<Object> deleteUUStatements(@PathVariable String statementsId,
                                                   Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UUStatements statementsById = uuidNodeNetworkService.findStatementsById(statementsId);
        clientUUIDService.validateClientUUID(user.getUsername(), List.of(statementsById.getSubject(), statementsById.getObject()));
        return ResponseEntity.ok(uuidNodeNetworkService.softDeleteUUStatements(statementsId, user.getUsername()));
    }



}
