package io.recheck.uuidprotocol.common.firestore.index.model;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.List;

@Data
public class IndexResponse {
    private QueryScope queryScope;
    private List<IndexField> fields;
    private String name;
    private State state;
    private Density density;

    public String getId() {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return name.substring(name.lastIndexOf("/")+1);
    }

    public String getCollection() {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        String a = "collectionGroups/";
        return name.substring(name.lastIndexOf(a)+a.length(), name.lastIndexOf("/indexes"));
    }
}
