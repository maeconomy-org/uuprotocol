package io.recheck.uuidprotocol.nodenetwork.node.service;

import io.recheck.uuidprotocol.common.security.X509UserDetails;
import io.recheck.uuidprotocol.domain.node.dto.UUFileDTO;
import io.recheck.uuidprotocol.domain.node.model.UUFile;
import io.recheck.uuidprotocol.domain.statements.dto.UUStatementDTO;
import io.recheck.uuidprotocol.domain.statements.model.UUStatementPredicate;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener.AggregateUUFileEventListener;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.UUFileDataSource;
import io.recheck.uuidprotocol.nodenetwork.owner.UUIDOwnerService;
import io.recheck.uuidprotocol.nodenetwork.statements.UUStatementsService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class UUFileNodeNetworkService extends NodeNetworkService<UUFile, UUFileDTO>{

    private final Path storageDir;
    private final String serverAddress;
    private final UUStatementsService uuStatementsService;

    @SneakyThrows
    public UUFileNodeNetworkService(@Value("${UUFileNodeNetworkService.storageDirProperty}") String storageDirProperty,
                                    @Value("${UUFileNodeNetworkService.serverAddressProperty}") String serverAddressProperty,
                                    UUFileDataSource uuFileDataSource,
                                    AggregateUUFileEventListener aggregateUUFileEventListener,
                                    UUIDOwnerService uuidOwnerService,
                                    UUStatementsService uuStatementsService) {
        super(uuFileDataSource, aggregateUUFileEventListener, uuidOwnerService);
        this.uuStatementsService = uuStatementsService;
        serverAddress = serverAddressProperty;
        storageDir = Paths.get(storageDirProperty);
        if (! new UrlResource(storageDir.toUri()).exists()) {
            Files.createDirectory(storageDir);
        }
    }

    @SneakyThrows
    public UUFile storeOrReplaceFile(String uuidToAttach, String uuidFile, MultipartFile file, X509UserDetails user) {
        UUFileDTO uuFileDTO = new UUFileDTO();
        uuFileDTO.setUuid(uuidFile);
        uuFileDTO.setFileName(file.getOriginalFilename());
        uuFileDTO.setContentType(file.getContentType());
        uuFileDTO.setSize(file.getSize());
        uuFileDTO.setFileReference(String.format("%s/api/UUFile/download/%s", serverAddress, uuidFile));

        UUFile uuFile = softDeleteAndCreate(uuFileDTO, user.getCertificate().getCertificateSha256());

        UUStatementDTO uuStatementDTO = new UUStatementDTO();
        uuStatementDTO.setSubject(uuidToAttach);
        uuStatementDTO.setPredicate(UUStatementPredicate.HAS_FILE);
        uuStatementDTO.setObject(uuidFile);
        uuStatementsService.findOrCreateWithOpposite(List.of(uuStatementDTO), user.getCertificate().getCertificateSha256());

        // Save new file
        Path fileUUIDDirectory = storageDir.resolve(uuidFile);
        if (! new UrlResource(fileUUIDDirectory.toUri()).exists()) {
            Files.createDirectory(fileUUIDDirectory);
        }

        Path fileCreatedAtDirectory = fileUUIDDirectory.resolve(String.valueOf(uuFile.getCreatedAt().getEpochSecond()));
        Files.createDirectory(fileCreatedAtDirectory);
        Path fileReference = fileCreatedAtDirectory.resolve(file.getOriginalFilename());

        Files.copy(file.getInputStream(), fileReference, StandardCopyOption.REPLACE_EXISTING);

        return uuFile;
    }


    @SneakyThrows
    public ResponseEntity<Resource> downloadFile(X509UserDetails user, String uuid) {
        uuidOwnerService.validateOwnerUUID(user.getCertificate().getCertificateSha256(), uuid);
        UUFile last = dataSource.findLast(uuid);
        if (last == null) {
            return ResponseEntity.notFound().build();
        }
        Path fileUUIDDirectory = storageDir.resolve(uuid);
        Path fileCreatedAtDirectory = fileUUIDDirectory.resolve(String.valueOf(last.getCreatedAt().getEpochSecond()));
        Path fileReference = fileCreatedAtDirectory.resolve(last.getFileName());
        Resource file = new UrlResource(Path.of(fileReference.toString()).toUri());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(last.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + last.getFileName() + "\"")
                .body(file);
    }

}
