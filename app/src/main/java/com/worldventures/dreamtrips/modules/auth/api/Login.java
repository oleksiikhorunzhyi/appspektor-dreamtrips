package com.worldventures.dreamtrips.modules.auth.api;

import com.worldventures.dreamtrips.core.api.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.common.model.Session;

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
