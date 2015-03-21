package com.worldventures.dreamtrips.modules.auth.model;

import com.worldventures.dreamtrips.modules.common.model.Session;
import com.worldventures.dreamtrips.modules.common.model.S3GlobalConfig;

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
