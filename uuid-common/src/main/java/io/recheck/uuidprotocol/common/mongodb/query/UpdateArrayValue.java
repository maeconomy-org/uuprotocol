package io.recheck.uuidprotocol.common.mongodb.query;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Update;

@RequiredArgsConstructor
public class UpdateArrayValue {

    private final String path;

    public String getParentPath() {
        String parentPath = "";
        if (path.lastIndexOf(".") > 0) {
            parentPath = path.substring(0,path.lastIndexOf("."));
        }
        return parentPath;
    }

    public Update pushArrayValue(Object value) {
        return new Update().push(path, value);
    }

    public Update pullArrayValue(Object value) {
        return new Update().pull(path, value);
    }

}
