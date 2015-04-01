package com.worldventures.dreamtrips.modules.common.model;

public class Session extends BaseEntity {
    String token;
    User user;
    Session innerSession;
    String sso_token;

    public String getSso_token() {
        return sso_token;
    }

    public void setSso_token(String sso_token) {
        this.sso_token = sso_token;
    }

    public Session getSession() {
        return innerSession;
    }

    public void setSession(Session session) {
        this.innerSession = session;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}