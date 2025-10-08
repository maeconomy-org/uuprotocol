package io.recheck.uuidprotocol.common.firestore.index.model;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Data
public class IndexOperationMetadata {

    private String index;
    private State state;
    private Instant startTime;

    public String getId() {
        if (!StringUtils.hasText(index)) {
            return null;
        }
        return index.substring(index.lastIndexOf("/")+1);
    }

    public String getCollection() {
        if (!StringUtils.hasText(index)) {
            return null;
        }
        String a = "collectionGroups/";
        return index.substring(index.lastIndexOf(a)+a.length(), index.lastIndexOf("/indexes"));
    }

}
