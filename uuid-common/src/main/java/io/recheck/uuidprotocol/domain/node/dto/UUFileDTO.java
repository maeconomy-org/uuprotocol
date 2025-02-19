package io.recheck.uuidprotocol.domain.node.dto;

import io.recheck.uuidprotocol.domain.node.model.UUFile;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

@Data
@EqualsAndHashCode(callSuper=false)
public class UUFileDTO extends NodeDTO<UUFile> {

    @NotBlank
    private String fileName;

    @NotBlank
    private String fileReference;

    private String label;

    @Override
    public UUFile build() {
        UUFile uuFile = new UUFile();
        BeanUtils.copyProperties(this, uuFile);
        return uuFile;
    }
}
