package io.recheck.uuidprotocol.common.resttemplate.model;

import lombok.Data;

@Data
public class ServerSpec {

    private String baseAddress;
    private Ssl ssl;

    @Data
    public static class Ssl {
        private String bundle;
        private String alias;
    }

}