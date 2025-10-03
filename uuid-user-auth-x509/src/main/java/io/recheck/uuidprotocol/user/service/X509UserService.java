package io.recheck.uuidprotocol.user.service;

import io.recheck.uuidprotocol.user.domain.X509UserDetails;
import io.recheck.uuidprotocol.user.persistence.X509UserDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class X509UserService {

    private final X509UserDataSource x509UserDataSource;

    public X509UserDetails create(X509UserDetails user) {
        String uuid = UUID.randomUUID().toString();
        user.setUserUUID(uuid);
        user.setCreatedAt(Instant.now());
        x509UserDataSource.create(user);
        return user;
    }

}
