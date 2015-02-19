package com.worldventures.dreamtrips.presentation;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.LoginHelper;
import com.worldventures.dreamtrips.core.session.AppSessionHolder;
import com.worldventures.dreamtrips.utils.ValidationUtils;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginFragmentPresentation extends BaseActivityPresentation<LoginFragmentPresentation.View> {

    @Inject
    DreamTripsApi dreamTripsApi;

    @Inject
    AppSessionHolder appSessionHolder;

    @Inject
    LoginHelper loginHelper;

    public LoginFragmentPresentation(View view) {
        super(view);
    }

    public void loginAction() {

        String username = view.getUsername();
        String userPassword = view.getUserPassword();

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
        }, username, userPassword);

        this.view.showProgressDialog();
    }

    public void fillDataAction() {
        if (BuildConfig.DEBUG && !BuildConfig.FLAVOR.equals("prod")) {
            view.setUsername("888888");
            view.setUserPassword("travel1ns1de");
        }
    }

    public static interface View extends BasePresentation.View {
        void showProgressDialog();

        void showLoginSuccess();

        void showLoginErrorMessage();

        public void showLocalErrors(String userNameError, String passwordError);

        String getUsername();

        void setUsername(String s);

        String getUserPassword();

        void setUserPassword(String travel1ns1de);
    }
}
