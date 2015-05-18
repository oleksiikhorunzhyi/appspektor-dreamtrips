package com.worldventures.dreamtrips.modules.auth.presenter;

import android.text.TextUtils;

import com.worldventures.dreamtrips.core.preference.Prefs;
import com.techery.spares.utils.ValidationUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.util.ValidationUtils.isPasswordValid;
import static com.worldventures.dreamtrips.util.ValidationUtils.isUsernameValid;

public class LoginPresenter extends ActivityPresenter<LoginPresenter.View> {

    @Inject
    protected Prefs prefs;

    private boolean isTermsAccepted;

    @Override
    public void onResume() {
        super.onResume();
        isTermsAccepted = prefs.getBoolean(Prefs.TERMS_ACCEPTED);

        if (!isTermsAccepted) {
            view.showTerms();
        } else {
            view.hideTerms();
        }
    }

    public void loginAction() {

        String username = view.getUsername();
        String userPassword = view.getUserPassword();

        ValidationUtils.VResult usernameValid = isUsernameValid(username);
        ValidationUtils.VResult passwordValid = isPasswordValid(userPassword);

        if (!usernameValid.isValid() || !passwordValid.isValid()) {
            view.showLocalErrors(usernameValid.getMessage(), passwordValid.getMessage());
            return;
        }

        if (!isTermsAccepted && !view.isTermsChecked()) {
            return;
        }

        dreamSpiceManager.loginUser(userPassword, username, (loginResponse, error) -> {
            if (error != null) {
                if (TextUtils.isEmpty(error.getMessage())) {
                    view.showLoginErrorMessage();
                } else {
                    view.alert(error.getMessage());
                }
            } else {
                prefs.put(Prefs.TERMS_ACCEPTED, true);
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

        void showTerms();

        void hideTerms();

        boolean isTermsChecked();
    }
}
