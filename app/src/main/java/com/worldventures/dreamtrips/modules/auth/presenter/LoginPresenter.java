package com.worldventures.dreamtrips.modules.auth.presenter;

import android.text.TextUtils;

import com.messenger.synchmechanism.MessengerConnector;
import com.techery.spares.utils.ValidationUtils;
import com.worldventures.dreamtrips.core.preference.StaticPageHolder;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.api.StaticPagesQuery;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.Locale;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.util.ValidationUtils.isPasswordValid;
import static com.worldventures.dreamtrips.util.ValidationUtils.isUsernameValid;

public class LoginPresenter extends Presenter<LoginPresenter.View> {

    @Inject
    StaticPageHolder staticPageHolder;
    @Inject
    LocaleHelper localeHelper;

    public void loginAction() {
        String username = view.getUsername();
        String userPassword = view.getUserPassword();

        ValidationUtils.VResult usernameValid = isUsernameValid(username);
        ValidationUtils.VResult passwordValid = isPasswordValid(userPassword);

        if (!usernameValid.isValid() || !passwordValid.isValid()) {
            view.showLocalErrors(usernameValid.getMessage(), passwordValid.getMessage());
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
                User user = loginResponse.getSession().getUser();
                TrackingHelper.login(user.getEmail());
                TrackingHelper.setUserId(Integer.toString(user.getId()));

                Locale userLocale = localeHelper.getAccountLocale(user);
                if (userLocale == null) userLocale = Locale.getDefault();
                StaticPagesQuery staticPagesQuery = new StaticPagesQuery(userLocale.getCountry(), userLocale.getLanguage());
                doRequest(staticPagesQuery, staticPageConfig -> {

                    staticPageHolder.put(staticPageConfig);
                    view.showLoginSuccess();
                    if (appSessionHolder.get().get().getGlobalConfig() != null) {
                        MessengerConnector.getInstance().connectAfterGlobalConfig();

                        activityRouter.openMain();
                        activityRouter.finish();
                    } else {
                        activityRouter.openLaunch();
                        activityRouter.finish();
                    }
                });
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
    }
}
