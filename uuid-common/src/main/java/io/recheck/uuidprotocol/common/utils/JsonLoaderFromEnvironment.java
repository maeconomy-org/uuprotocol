package io.recheck.uuidprotocol.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class JsonLoaderFromEnvironment {

    private final Environment environment;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    @Autowired
    public JsonLoaderFromEnvironment(Environment environment,
                                     ResourceLoader resourceLoader,
                                     ObjectMapper objectMapper) {
        this.environment = environment;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> loadJsonPropertyAsMap(String propertyName) throws IOException {
        // Get property value, e.g., "classpath:myfile.json"
        String location = environment.getProperty(propertyName);
        if (location == null) {
            throw new IllegalArgumentException("Property " + propertyName + " not found");
        }

        // Load resource
        Resource resource = resourceLoader.getResource(location);

        if (!resource.exists()) {
            throw new IllegalArgumentException("Resource " + location + " not found");
        }

        // Parse JSON into Map
        return objectMapper.readValue(resource.getInputStream(), new TypeReference<Map<String, Object>>() {});
    }

}