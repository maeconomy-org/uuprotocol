package io.recheck.uuidprotocol.common.resttemplate.model;

import lombok.Data;

@Data
public class SSLContextSpec {

    private String keyStoreFile;
    private String keystorePassword;
    private String keyStoreAlias;
    private String trustStoreFile;
    private String truststorePassword;

}
