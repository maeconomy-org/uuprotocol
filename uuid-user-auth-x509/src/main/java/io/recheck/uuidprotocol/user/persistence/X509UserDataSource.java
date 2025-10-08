package io.recheck.uuidprotocol.user.persistence;

import io.recheck.uuidprotocol.common.firestore.FirestoreDataSource;
import io.recheck.uuidprotocol.common.firestore.model.WrapUnaryEqualToFilter;
import io.recheck.uuidprotocol.user.domain.X509UserDetails;
import org.springframework.stereotype.Service;


@Service
public class X509UserDataSource extends FirestoreDataSource<X509UserDetails> {

    WrapUnaryEqualToFilter credentialsFilter = new WrapUnaryEqualToFilter("credentials");

    public X509UserDataSource() {
        super(X509UserDetails.class);
    }

    public X509UserDetails findByCredentials(String credentials) {
        return whereFindFirst(credentialsFilter.toFirestoreFilter(credentials));
    }

}