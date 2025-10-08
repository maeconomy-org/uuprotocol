package io.recheck.uuidprotocol.common.firestore.index;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Query;
import io.recheck.uuidprotocol.common.firestore.index.model.*;
import io.recheck.uuidprotocol.common.resttemplate.RestTemplateImpl;
import io.recheck.uuidprotocol.common.resttemplate.model.RequestSpec;
import io.recheck.uuidprotocol.common.resttemplate.model.ResponseSpec;
import lombok.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Before execution :
 * 1. Enable API for the project - https://console.cloud.google.com/apis/enableflow;apiid=firestore.googleapis.com
 * 2. Add "Cloud Datastore IndexRequest Admin" role to the service account defined at service-account-....json at client_email property
 **/

@Service
@RequiredArgsConstructor
public class FirestoreGoogleApiService {

    private final RestTemplateImpl restTemplateImplFirestoreGoogleApi;
    private final GoogleCredentials googleCredentials;

    private GoogleCredentials googleCredentialsScoped;

    @PostConstruct
    public void initGoogleCredentialsScoped() {
        googleCredentialsScoped = googleCredentials.createScoped("https://www.googleapis.com/auth/datastore");
    }

    public ListIndexes listIndexes() {
        RequestSpec<?, ListIndexes> requestSpec = new RequestSpec<>();
        requestSpec.setHttpMethod(HttpMethod.GET);

        requestSpec.setResourceAddress(
                "/databases/(default)/collectionGroups/<collection>/indexes");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + refreshAndGetAccessToken());
        requestSpec.setHttpHeaders(headers);

        requestSpec.setResponseBodyClass(ListIndexes.class);

        ResponseSpec<ListIndexes> responseSpec = restTemplateImplFirestoreGoogleApi.send(requestSpec);
        return responseSpec.getBody();
    }

    public IndexResponse getIndex(String index_id) {
        RequestSpec<?, IndexResponse> requestSpec = new RequestSpec<>();
        requestSpec.setHttpMethod(HttpMethod.GET);

        requestSpec.setResourceAddress(
                "/databases/(default)/collectionGroups/<collection>/indexes/"+index_id);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + refreshAndGetAccessToken());
        requestSpec.setHttpHeaders(headers);

        requestSpec.setResponseBodyClass(IndexResponse.class);

        ResponseSpec<IndexResponse> responseSpec = restTemplateImplFirestoreGoogleApi.send(requestSpec);
        return responseSpec.getBody();
    }

    public Operation createIndex(String collectionId, List<IndexField> fields) {
        RequestSpec<IndexRequest, Operation> requestSpec = new RequestSpec<>();

        requestSpec.setHttpMethod(HttpMethod.POST);

        requestSpec.setResourceAddress(
                "/databases/(default)/collectionGroups/<collection>/indexes"
                .replace("<collection>", collectionId));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + refreshAndGetAccessToken());
        requestSpec.setHttpHeaders(headers);

        requestSpec.setBody(new IndexRequest(QueryScope.COLLECTION, fields));

        requestSpec.setResponseBodyClass(Operation.class);


        try {
            ResponseSpec<Operation> responseSpec = restTemplateImplFirestoreGoogleApi.send(requestSpec);
            return responseSpec.getBody();
        } catch (HttpClientErrorException.Conflict e) {
            if (e.getMessage().contains("409 Conflict on POST request") &&
                e.getMessage().contains("\"message\": \"index already exists\"")) {
                return e.getResponseBodyAs(Operation.class);
            }
            throw e;
        }

    }

    public void deleteIndex(String index_id) {
        RequestSpec<?, ?> requestSpec = new RequestSpec<>();
        requestSpec.setHttpMethod(HttpMethod.DELETE);

        requestSpec.setResourceAddress(
                "/databases/(default)/collectionGroups/<collection>/indexes/"+index_id);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + refreshAndGetAccessToken());
        requestSpec.setHttpHeaders(headers);

        restTemplateImplFirestoreGoogleApi.send(requestSpec);
    }

    @SneakyThrows
    private String refreshAndGetAccessToken() {
        googleCredentialsScoped.refreshIfExpired();
        AccessToken token = googleCredentialsScoped.getAccessToken();
        return token.getTokenValue();
    }

}
