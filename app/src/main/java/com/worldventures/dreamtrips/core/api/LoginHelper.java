package com.worldventures.dreamtrips.core.api;

import com.google.gson.JsonObject;
import com.techery.spares.module.Annotations.Global;
import com.techery.spares.module.Annotations.Private;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.core.model.config.S3GlobalConfig;
import com.worldventures.dreamtrips.core.model.config.ServerStatus;
import com.worldventures.dreamtrips.core.session.AppSessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.utils.busevents.ServerConfigError;
import com.worldventures.dreamtrips.utils.busevents.UpdateUserInfoEvent;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginHelper {

    @Inject
    @Private
    DreamTripsApi dreamTripsApi;

    @Inject
    WorldVenturesApi worldVenturesApi;

    @Inject
    AppSessionHolder appSessionHolder;

    @Inject
    S3Api s3Api;

    @Inject
    @Global
    EventBus eventBus;

    public LoginHelper(Injector baseActivity) {
        baseActivity.inject(this);
    }

    public <T> void login(final Executor<T> executor, final Callback<T> callback, final String username, final String userPassword) {
        tryGetConfigAsync(new Callback<S3GlobalConfig>() {
            @Override
            public void success(S3GlobalConfig s3GlobalConfig, Response response) {
                if (isServerUp(s3GlobalConfig)) {
                    tryLoginAsync(new Callback<Session>() {
                        @Override
                        public void success(Session session, Response response) {
                            tryGetLegacyTokenAsync(new Callback<JsonObject>() {
                                @Override
                                public void success(JsonObject jsonObject, Response response) {
                                    if (handleSession(session, getStringToken(jsonObject), s3GlobalConfig, username, userPassword))
                                        executor.execute(callback);
                                }

                                @Override
                                public void failure(RetrofitError e) {
                                    callback.failure(e);
                                }
                            }, username, userPassword);
                        }

                        @Override
                        public void failure(RetrofitError e) {
                            callback.failure(e);
                        }
                    }, username, userPassword);
                } else {
                    callback.failure(null);
                }
            }

            @Override
            public void failure(RetrofitError e) {
                callback.failure(e);
            }
        });
    }

    private boolean isServerUp(S3GlobalConfig s3GlobalConfig) {
        ServerStatus.Status serv = s3GlobalConfig.getServerStatus().getProduction();
        String status = serv.getStatus();
        String message = serv.getMessage();
        if (!status.equalsIgnoreCase("up")) {
            String s = String.format("Server is %s, message = %s", status, message);
            eventBus.post(new ServerConfigError(s));
            return false;
        }
        return true;
    }


    public boolean loginSync(String username, String userPassword) {
        Session session = tryLoginSync(username, userPassword);
        String token = tryGetLegacyTokenSync(username, userPassword);
        S3GlobalConfig s3GlobalConfig = tryGetConfigSync();

        if (handleSession(session, token, s3GlobalConfig, username, userPassword))
            return true;
        return false;
    }


    private S3GlobalConfig tryGetConfigSync() {
        return s3Api.getConfig();
    }

    public String tryGetLegacyTokenSync(String username, String userPassword) {
        JsonObject jsonObject = worldVenturesApi.getToken(username, userPassword);
        return getStringToken(jsonObject);
    }


    private Session tryLoginSync(String username, String userPassword) {
        return dreamTripsApi.login(username, userPassword);
    }

    private void tryGetLegacyTokenAsync(Callback<JsonObject> callback, String username, String userPassword) {
        worldVenturesApi.getToken(username, userPassword, callback);
    }


    private void tryLoginAsync(Callback<Session> callback, String username, String userPassword) {
        dreamTripsApi.login(username, userPassword, callback);
    }

    private void tryGetConfigAsync(Callback<S3GlobalConfig> callback) {
        s3Api.getConfig(callback);
    }

    private boolean handleSession(Session session, String legacyToken, S3GlobalConfig globalConfig, String username, String userPassword) {
        String sessionToken = session.getToken();
        User sessionUser = session.getUser();

        UserSession userSession = new UserSession();
        userSession.setUser(sessionUser);
        userSession.setApiToken(sessionToken);
        userSession.setLegacyApiToken(legacyToken);

        userSession.setUsername(username);
        userSession.setUserPassword(userPassword);
        userSession.setLastUpdate(System.currentTimeMillis());

        userSession.setGlobalConfig(globalConfig);

        if (sessionUser != null & sessionToken != null) {
            appSessionHolder.put(userSession);
            return true;
        }
        eventBus.post(new UpdateUserInfoEvent());
        return false;
    }

    private String getStringToken(JsonObject jsonObject) {
        return jsonObject.get("result").getAsString();
    }

}
