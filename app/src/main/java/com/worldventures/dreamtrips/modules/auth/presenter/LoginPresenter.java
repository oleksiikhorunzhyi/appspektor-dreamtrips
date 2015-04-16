package com.worldventures.dreamtrips.modules.auth.presenter;

import android.text.TextUtils;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.core.utils.ValidationUtils;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public class LoginPresenter extends ActivityPresenter<LoginPresenter.View> {

    public LoginPresenter(View view) {
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

        dreamSpiceManager.login(userPassword, username, (loginResponse, error) -> {
            if (error != null) {
                if (TextUtils.isEmpty(error.getMessage())) {
                    view.showLoginErrorMessage();
                } else {
                    view.alert(error.getMessage());
                }
            } else {
                TrackingHelper.login(loginResponse.getSession().getUser().getEmail());
                activityRouter.openMain();
                activityRouter.finish();
                view.showLoginSuccess();
            }
        });

        this.view.showProgressDialog();
    }

    public static interface View extends Presenter.View {
        void showProgressDialog();

        void showLoginSuccess();

        void showLoginErrorMessage();

        public void showLocalErrors(int userNameError, int passwordError);

        String getUsername();

        String getUserPassword();
    }
}
