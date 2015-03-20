package com.worldventures.dreamtrips.core.model.response;

import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.config.S3GlobalConfig;

public class LoginResponse {
    S3GlobalConfig config;
    Session session;

    public S3GlobalConfig getConfig() {
        return config;
    }

    public void setConfig(S3GlobalConfig config) {
        this.config = config;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
