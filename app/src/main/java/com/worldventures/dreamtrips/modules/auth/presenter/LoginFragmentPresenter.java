package com.worldventures.dreamtrips.modules.auth.presenter;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.modules.common.presenter.BaseActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;
import com.worldventures.dreamtrips.core.utils.AdobeTrackingHelper;
import com.worldventures.dreamtrips.core.utils.ValidationUtils;

import javax.inject.Inject;

public class LoginFragmentPresenter extends BaseActivityPresenter<LoginFragmentPresenter.View> {

    @Inject
    DreamTripsApi dreamTripsApi;

    public LoginFragmentPresenter(View view) {
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
        dreamSpiceManager.login((l, e) -> {
            if (e != null) {
                view.showLoginErrorMessage();
            } else {
                AdobeTrackingHelper.login(l.getSession().getUser().getEmail());
                activityRouter.openMain();
                activityRouter.finish();
                view.showLoginSuccess();
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

    public static interface View extends BasePresenter.View {
        void showProgressDialog();

        void showLoginSuccess();

        void showLoginErrorMessage();

        public void showLocalErrors(int userNameError, int passwordError);

        String getUsername();

        void setUsername(String s);

        String getUserPassword();

        void setUserPassword(String travel1ns1de);
    }
}
