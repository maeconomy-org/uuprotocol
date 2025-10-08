package io.recheck.uuidprotocol.common.firestore.index;

import io.recheck.uuidprotocol.common.resttemplate.RestTemplateImpl;
import io.recheck.uuidprotocol.common.resttemplate.config.RestTemplateImplBuilder;
import io.recheck.uuidprotocol.common.resttemplate.model.ServerSpec;
import io.recheck.uuidprotocol.common.utils.JsonLoaderFromEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FirestoreGoogleApiServiceConfig {

    private final JsonLoaderFromEnvironment jsonLoaderFromEnvironment;

    @Bean
    @SneakyThrows
    public RestTemplateImpl restTemplateImplFirestoreGoogleApi(ServerSpec serverSpecFirestoreGoogleApi) {
        Map<String, Object> map = jsonLoaderFromEnvironment.loadJsonPropertyAsMap("gcp.firebase.service-account");

        serverSpecFirestoreGoogleApi.setBaseAddress(serverSpecFirestoreGoogleApi.getBaseAddress().replace("<project_id>", map.get("project_id").toString()));

        return RestTemplateImplBuilder.build(serverSpecFirestoreGoogleApi);
    }


    @Bean
    public ServerSpec serverSpecFirestoreGoogleApi() {
        ServerSpec serverSpec = new ServerSpec();
        serverSpec.setBaseAddress("https://firestore.googleapis.com/v1/projects/<project_id>");
        return serverSpec;
    }
}