package com.worldventures.dreamtrips.api.http.executor;

import com.worldventures.dreamtrips.api.http.provider.AuthorizedJanetProvider;
import com.worldventures.dreamtrips.api.session.LoginHttpAction;
import com.worldventures.dreamtrips.api.session.model.Session;

public class AuthorizedActionExecutor extends BaseActionExecutor<AuthorizedJanetProvider> {

    private final String username;

    private final String password;

    public AuthorizedActionExecutor(String username, String password) {
        super(new AuthorizedJanetProvider());
        this.username = username;
        this.password = password;
    }

    public LoginHttpAction authorize() {
        return execute(new LoginHttpAction(username, password));
    }

    public Session getSession() {
        return getJanetProvider().session();
    }
}
