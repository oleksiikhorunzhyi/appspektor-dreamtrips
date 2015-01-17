package com.worldventures.dreamtrips.presentation;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.WorldVenturesApi;
import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.core.session.AppSessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.utils.ValidationUtils;

import org.robobinding.annotation.PresentationModel;
import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@PresentationModel
public class LoginFragmentPresentation extends BasePresentation<LoginFragmentPresentation.View> implements HasPresentationModelChangeSupport {
    private final PresentationModelChangeSupport changeSupport;

    @Inject
    DreamTripsApi dreamTripsApi;

    @Inject
    WorldVenturesApi worldVenturesApi;

    @Inject
    AppSessionHolder appSessionHolder;

    private String username;
    private String userPassword;

    public LoginFragmentPresentation(View view) {
        super(view);
        this.changeSupport = new PresentationModelChangeSupport(this);
    }

    public void loginAction() {

        String username = getUsername();
        String userPassword = getUserPassword();

        ValidationUtils.VResult usernameValid = ValidationUtils.isUsernameValid(username);
        ValidationUtils.VResult passwordValid = ValidationUtils.isPasswordValid(userPassword);
        if (!usernameValid.isValid() || !passwordValid.isValid()) {
            view.showLocalErrors(usernameValid.getMessage(), passwordValid.getMessage());
            return;
        }

        this.view.showProgressDialog();

        dreamTripsApi.login(username, userPassword, new Callback<Session>() {
            @Override
            public void success(Session session, Response response) {
                String sessionToken = session.getToken();
                User sessionUser = session.getUser();

                UserSession userSession = new UserSession();
                userSession.setUser(sessionUser);
                userSession.setApiToken(sessionToken);

                if (sessionUser == null || sessionToken == null) {
                    LoginFragmentPresentation.this.view.showLoginErrorMessage();
                    return;
                }

                worldVenturesApi.getToken(username, userPassword, new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject, Response response) {
                        String token = jsonObject.get("result").getAsString();

                        userSession.setLegacyApiToken(token);

                        appSessionHolder.put(userSession);

                        activityRouter.openMain();
                        activityRouter.finish();
                        LoginFragmentPresentation.this.view.showLoginSuccess();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        LoginFragmentPresentation.this.view.showLoginErrorMessage();
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public PresentationModelChangeSupport getPresentationModelChangeSupport() {
        return changeSupport;
    }

    public void fillDataAction() {
        if (BuildConfig.DEBUG) {
            setUsername("888888");
            setUserPassword("travel1ns1de");
            changeSupport.firePropertyChange("username");
            changeSupport.firePropertyChange("userPassword");
        }
    }

    public static interface View extends BasePresentation.View {
        void showProgressDialog();

        void showLoginSuccess();

        void showLoginErrorMessage();

        public void showLocalErrors(String userNameError, String passwordError);
    }
}
