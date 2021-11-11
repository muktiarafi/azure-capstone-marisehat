package dev.muktiarafi.marisehat.model;

import com.microsoft.graph.models.PasswordProfile;
import com.microsoft.graph.models.User;
import lombok.Builder;

public class MSUser extends User {

    @Builder
    public MSUser(
            boolean accountEnabled,
            String displayName,
            String mailNickname,
            String userPrincipalName,
            PasswordProfile passwordProfile
    ) {
        this.accountEnabled = accountEnabled;
        this.displayName = displayName;
        this.mailNickname = mailNickname;
        this.userPrincipalName = userPrincipalName;
        this.passwordProfile = passwordProfile;
    }
}
