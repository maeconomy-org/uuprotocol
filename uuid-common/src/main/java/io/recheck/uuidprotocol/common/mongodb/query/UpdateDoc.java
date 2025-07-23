package io.recheck.uuidprotocol.common.mongodb.query;

import io.recheck.uuidprotocol.common.mongodb.MongoUtils;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import java.util.Map;

@RequiredArgsConstructor
public class UpdateDoc {

    private final String path;

    public Update setDoc(Object objectDoc) {
        String fullPath = "";
        if (StringUtils.hasText(path)) {
            fullPath = path + ".";
        }

        Update update = new Update();
        Document updateObjectDoc = MongoUtils.convertToDocument(objectDoc);
        for (Map.Entry<String, Object> arrayDocEntry : updateObjectDoc.entrySet()) {
            update.set(fullPath + arrayDocEntry.getKey(), arrayDocEntry.getValue());
        }

        return update;
    }

    public Update unsetDoc() {
        return new Update().unset(path);
    }

}
