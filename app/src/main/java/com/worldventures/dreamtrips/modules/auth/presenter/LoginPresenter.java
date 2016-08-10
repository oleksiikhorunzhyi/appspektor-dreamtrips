package com.worldventures.dreamtrips.modules.auth.presenter;

import com.techery.spares.utils.ValidationUtils;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.auth.api.command.LoginCommand;
import com.worldventures.dreamtrips.modules.auth.service.LoginInteractor;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

import static com.worldventures.dreamtrips.util.ValidationUtils.isPasswordValid;
import static com.worldventures.dreamtrips.util.ValidationUtils.isUsernameValid;

public class LoginPresenter extends Presenter<LoginPresenter.View> {

    @Inject LoginInteractor loginInteractor;

    public void loginAction() {
        String username = view.getUsername();
        String userPassword = view.getUserPassword();

        ValidationUtils.VResult usernameValid = isUsernameValid(username);
        ValidationUtils.VResult passwordValid = isPasswordValid(userPassword);

        if (!usernameValid.isValid() || !passwordValid.isValid()) {
            view.showLocalErrors(usernameValid.getMessage(), passwordValid.getMessage());
            return;
        }

        view.bindUntilDropView(loginInteractor.loginActionPipe()
                .createObservable(new LoginCommand(username, userPassword))
                .compose(new IoToMainComposer<>()))
                .subscribe(new ActionStateSubscriber<LoginCommand>()
                        .onSuccess(loginCommand -> {
                            User user = loginCommand.getResult().getUser();
                            TrackingHelper.login(user.getEmail());
                            TrackingHelper.setUserId(Integer.toString(user.getId()));

                            activityRouter.openLaunch();
                            activityRouter.finish();
                        }).onFail((loginCommand, throwable) -> {
                            TrackingHelper.loginError();
                            view.alert(loginCommand.getErrorMessage());
                        }));
        view.showProgressDialog();
    }

    public interface View extends RxView {

        void showProgressDialog();

        void showLocalErrors(int userNameError, int passwordError);

        String getUsername();

        String getUserPassword();
    }
}
