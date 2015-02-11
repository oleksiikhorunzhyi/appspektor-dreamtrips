package com.worldventures.dreamtrips.presentation;

import com.worldventures.dreamtrips.core.api.S3Api;
import com.worldventures.dreamtrips.core.model.config.S3GlobalConfig;
import com.worldventures.dreamtrips.core.model.config.ServerStatus;
import com.worldventures.dreamtrips.core.session.UserSession;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BaseActivityPresentation<VT extends BasePresentation.View> extends BasePresentation<VT> {
    public BaseActivityPresentation(VT view) {
        super(view);
    }

    @Inject
    protected S3Api s3Api;

    protected void loadS3Config() {
        s3Api.getConfig(new Callback<S3GlobalConfig>() {
            @Override
            public void success(S3GlobalConfig config, Response response) {
                UserSession userSession = appSessionHolder.get().get();
                if (userSession == null) userSession = new UserSession();
                userSession.setGlobalConfig(config);
                appSessionHolder.put(userSession);
                ServerStatus.Status serv = config.getServerStatus().getProduction();
                String status = serv.getStatus();
                String message = serv.getMessage();
                if (!status.equalsIgnoreCase("up")) {
                    String s = String.format("Server is %s, message = %s", status, message);
                    view.alert(s);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
