package dev.muktiarafi.marisehat.model;

import com.microsoft.graph.models.PasswordProfile;
import lombok.Builder;

public class MSPasswordProfile extends PasswordProfile {

    @Builder
    public MSPasswordProfile(boolean forceChangePasswordNextSignIn, String password) {
        this.forceChangePasswordNextSignIn = forceChangePasswordNextSignIn;
        this.password = password;
    }
}
