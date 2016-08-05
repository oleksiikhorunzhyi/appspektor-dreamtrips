package com.worldventures.dreamtrips.modules.auth.api.command;

import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.api.action.LoginAction;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.modules.common.model.Session;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class LoginCommand extends CommandWithError<Session> implements InjectableAction {

    @Inject Janet janet;
    @Inject SessionHolder<UserSession> appSessionHolder;
    @Inject AuthInteractor authInteractor;

    private String userName;
    private String userPassword;

    public LoginCommand() {
        if (isCredentialExists()) {
            UserSession userSession = appSessionHolder.get().get();
            this.userName = userSession.getUsername();
            this.userPassword = userSession.getUserPassword();
        }
    }

    public LoginCommand(String userName, String userPassword) {
        this.userName = userName;
        this.userPassword = userPassword;
    }

    @Override
    public int getFallbackErrorMessage() {
        return R.string.error_fail_to_login;
    }

    @Override
    protected void run(CommandCallback<Session> callback) throws Throwable {
        janet.createPipe(LoginAction.class, Schedulers.io())
                .createObservableResult(new LoginAction(userName, userPassword))
                .map(LoginAction::getLoginResponse)
                .doOnNext(this::updateSession)
                .doOnNext(this::notifyUserUpdated)
                .subscribe(callback::onSuccess, callback::onFail);
    }

    private boolean isCredentialExists() {
        Optional<UserSession> userSessionOptional = appSessionHolder.get();
        if (userSessionOptional.isPresent()) {
            UserSession userSession = userSessionOptional.get();
            return userSession.getUsername() != null && userSession.getUserPassword() != null;
        } else {
            return false;
        }
    }

    private void updateSession(Session session) {
        String sessionToken = session.getToken();
        User sessionUser = session.getUser();

        UserSession userSession;
        if (appSessionHolder.get().isPresent()) {
            userSession = appSessionHolder.get().get();
        } else {
            userSession = new UserSession();
        }

        userSession.setUser(sessionUser);
        userSession.setApiToken(sessionToken);
        userSession.setLegacyApiToken(session.getSsoToken());

        userSession.setUsername(userName);
        userSession.setUserPassword(userPassword);
        userSession.setLastUpdate(System.currentTimeMillis());

        List<Feature> features = session.getPermissions();
        userSession.setFeatures(features);

        if (sessionUser != null & sessionToken != null) {
            appSessionHolder.put(userSession);
        }
    }

    private void notifyUserUpdated(Session session) {
        authInteractor.updateUserPipe().send(new UpdateUserCommand(session.getUser()));
    }
}
