package com.worldventures.dreamtrips.core.api.action;

import com.worldventures.dreamtrips.modules.common.model.Session;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/sessions", type = HttpAction.Type.SIMPLE, method = HttpAction.Method.POST)
public class LoginAction extends BaseHttpAction{

    @Body LoginBody loginBody;
    @Response Session loginResponse;

    public LoginAction(String username, String password) {
        this.loginBody = new LoginBody(username, password);
    }

    public Session getLoginResponse() {
        return loginResponse;
    }

    private class LoginBody {

        private final String username;
        private final String password;

        private LoginBody(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}
