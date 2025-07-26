package io.recheck.uuidprotocol.nodenetwork.node.service;

import io.recheck.uuidprotocol.common.security.X509UserDetails;
import io.recheck.uuidprotocol.domain.node.dto.UUFileDTO;
import io.recheck.uuidprotocol.domain.node.model.UUFile;
import io.recheck.uuidprotocol.nodenetwork.aggregate.persistence.listener.AggregateUUFileEventListener;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.UUFileDataSource;
import io.recheck.uuidprotocol.nodenetwork.owner.UUIDOwnerService;
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

@Service
public class UUFileNodeNetworkService extends NodeNetworkService<UUFile, UUFileDTO>{

    private final Path storageDir;

    @SneakyThrows
    public UUFileNodeNetworkService(@Value("${UUFileNodeNetworkService.storageDirProperty}") String storageDirProperty,
                                    UUFileDataSource uuFileDataSource,
                                    AggregateUUFileEventListener aggregateUUFileEventListener,
                                    UUIDOwnerService uuidOwnerService) {
        super(uuFileDataSource, aggregateUUFileEventListener, uuidOwnerService);
        storageDir = Paths.get(storageDirProperty);
        if (! new UrlResource(storageDir.toUri()).exists()) {
            Files.createDirectory(storageDir);
        }
    }

    @SneakyThrows
    public UUFile storeOrReplaceFile(String uuid, MultipartFile file, X509UserDetails user) {

        if (file.getSize() > 100 * 1024 * 1024) {
            throw new IllegalArgumentException("File too large (max 100MB)");
        }

        Path fileDirectory = storageDir.resolve(uuid);
        Path fileReference = fileDirectory.resolve(file.getOriginalFilename());

        // Handle replacement && history??
        UUFile existing = dataSource.findLast(uuid);
        if (existing != null) {
            //build filereference of old name , not from new
            Files.deleteIfExists(Path.of(existing.getFileReference()));
//            subtractUsage(user.getCertFingerprint(), existing.getSize());
            /*
            // delete file & subtractUsage ? or NOT delete file & NOT subtractUsage ?
            if (!existing.getSoftDeleted()) {

            }
            else {
            }
            */
        }

        UUFileDTO uuFileDTO = new UUFileDTO();
        uuFileDTO.setUuid(uuid);
        uuFileDTO.setFileName(file.getOriginalFilename());
        uuFileDTO.setContentType(file.getContentType());
        uuFileDTO.setSize(file.getSize());
        // fileReference : "filestorage\<uuid>\<filename>" ??
        // fileReference : "https://<server>/download/{uuid}" ??
        uuFileDTO.setFileReference(fileReference.toString());

        UUFile uuFile = softDeleteAndCreate(uuFileDTO, user.getCertFingerprint());



        // Save new file
        if (! new UrlResource(fileDirectory.toUri()).exists()) {
            Files.createDirectory(fileDirectory);
        }
        Files.copy(file.getInputStream(), fileReference, StandardCopyOption.REPLACE_EXISTING);
//        addUsage(user.getCertFingerprint(), file.getSize());

        return uuFile;
    }


    @SneakyThrows
    public ResponseEntity<Resource> downloadFile(String uuid) {
        UUFile last = dataSource.findLast(uuid);
        if (last == null) {
            return ResponseEntity.notFound().build();
        }
        Resource file = new UrlResource(Path.of(last.getFileReference()).toUri());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(last.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + last.getFileName() + "\"")
                .body(file);
    }

}
