package com.worldventures.dreamtrips.presentation;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.LoginHelper;
import com.worldventures.dreamtrips.core.api.WorldVenturesApi;
import com.worldventures.dreamtrips.core.session.AppSessionHolder;
import com.worldventures.dreamtrips.utils.ValidationUtils;

import org.robobinding.annotation.PresentationModel;
import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@PresentationModel
public class LoginFragmentPresentation extends BaseActivityPresentation<LoginFragmentPresentation.View> implements HasPresentationModelChangeSupport {
    private final PresentationModelChangeSupport changeSupport;

    @Inject
    DreamTripsApi dreamTripsApi;

    @Inject
    AppSessionHolder appSessionHolder;

    private String username;
    private String userPassword;
    @Inject
    LoginHelper loginHelper;

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

        loginHelper.login(callback -> callback.success(null, null), new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {
                activityRouter.openMain();
                activityRouter.finish();
                view.showLoginSuccess();
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null) {
                    view.showLoginErrorMessage();
                }
            }
        }, getUsername(), getUserPassword());

        this.view.showProgressDialog();
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
