package com.worldventures.dreamtrips.core.api.request.auth;

import com.worldventures.dreamtrips.core.api.request.base.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.Session;

public class Login extends DreamTripsRequest<Session> {

    private String username;
    private String password;

    public Login(String username, String password) {
        super(Session.class);
        this.username = username;
        this.password = password;
    }

    @Override
    public Session loadDataFromNetwork() throws Exception {
        return getService().login(username, password);
    }
}
