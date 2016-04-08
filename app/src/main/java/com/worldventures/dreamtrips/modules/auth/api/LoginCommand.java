package com.worldventures.dreamtrips.modules.auth.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.common.model.Session;

public class LoginCommand extends Command<Session> {

    private String username;
    private String password;

    public LoginCommand(String username, String password) {
        super(Session.class);
        this.username = username;
        this.password = password;
    }

    @Override
    public Session loadDataFromNetwork() throws Exception {
        return getService().login(username, password);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_login;
    }
}
