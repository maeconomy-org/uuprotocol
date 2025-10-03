package io.recheck.uuidprotocol.domain.audit;

import io.recheck.uuidprotocol.domain.user.UserDetailsCustom;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuditUser {

    private String userUUID;
    private String credentials;

    public AuditUser(UserDetailsCustom userDetailsCustom) {
        this.userUUID = userDetailsCustom.getUserUUID();
        this.credentials = (String) userDetailsCustom.getCredentials();
    }

}
