package io.recheck.uuidprotocol.user.persistence;

import com.google.cloud.firestore.Filter;
import io.recheck.uuidprotocol.common.firestore.FirestoreDataSource;
import io.recheck.uuidprotocol.user.domain.X509UserDetails;
import org.springframework.stereotype.Service;

@Service
public class X509UserDataSource extends FirestoreDataSource<X509UserDetails> {

    public X509UserDataSource() {
        super(X509UserDetails.class);
    }

    public X509UserDetails findByCredentials(String credentials) {
        return where(Filter.equalTo("credentials", credentials)).stream().findFirst().orElse(null);
    }


}
