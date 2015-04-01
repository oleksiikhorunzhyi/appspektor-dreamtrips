package com.worldventures.dreamtrips.modules.auth.model;

import com.worldventures.dreamtrips.modules.common.model.AppConfig;
import com.worldventures.dreamtrips.modules.common.model.Session;

public class LoginResponse {
    protected AppConfig config;
    protected Session session;

    public AppConfig getConfig() {
        return config;
    }

    public void setConfig(AppConfig config) {
        this.config = config;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
