package com.worldventures.dreamtrips.modules.auth.presenter;

import android.text.TextUtils;

import com.techery.spares.utils.ValidationUtils;
import com.worldventures.dreamtrips.core.utils.TermsConditionsValidator;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.util.ValidationUtils.isPasswordValid;
import static com.worldventures.dreamtrips.util.ValidationUtils.isUsernameValid;

public class LoginPresenter extends ActivityPresenter<LoginPresenter.View> {

    @Inject
    protected TermsConditionsValidator termsConditionsValidator;

    private boolean isTermsAccepted;

    @Override
    public void onResume() {
        super.onResume();
        isTermsAccepted = termsConditionsValidator.newVersionAccepted();

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
                TrackingHelper.loginError();
                if (TextUtils.isEmpty(error.getMessage())) {
                    view.showLoginErrorMessage();
                } else {
                    view.alert(error.getMessage());
                }
            } else {
                view.showLoginSuccess();
                User user = loginResponse.getSession().getUser();
                TrackingHelper.login(user.getEmail());
                TrackingHelper.setUserId(Integer.toString(user.getId()));

                if (appSessionHolder.get().get().getGlobalConfig() != null) {
                    termsConditionsValidator.setNewVersionAccepted(true);
                    activityRouter.openMain();
                    activityRouter.finish();
                } else {
                    activityRouter.openLaunch();
                    activityRouter.finish();
                }
            }
        });

        this.view.showProgressDialog();
    }

    public interface View extends Presenter.View {
        void showProgressDialog();

        void showLoginSuccess();

        void showLoginErrorMessage();

        void showLocalErrors(int userNameError, int passwordError);

        String getUsername();

        String getUserPassword();

        void showTerms();

        void hideTerms();

        boolean isTermsChecked();
    }
}
