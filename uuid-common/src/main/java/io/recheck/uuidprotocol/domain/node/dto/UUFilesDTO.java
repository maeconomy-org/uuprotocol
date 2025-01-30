package io.recheck.uuidprotocol.domain.node.dto;

import io.recheck.uuidprotocol.domain.node.model.UUFiles;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

@Data
@EqualsAndHashCode(callSuper=false)
public class UUFilesDTO extends NodeDTO<UUFiles> {

    @NotBlank
    private String fileName;

    @NotBlank
    private String fileReference;

    private String label;

    @Override
    public UUFiles build() {
        UUFiles uuFiles = new UUFiles();
        BeanUtils.copyProperties(this, uuFiles);
        return uuFiles;
    }
}
