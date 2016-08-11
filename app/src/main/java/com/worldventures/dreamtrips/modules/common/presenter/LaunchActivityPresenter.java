package com.worldventures.dreamtrips.modules.common.presenter;

import android.support.annotation.NonNull;

import com.github.pwittchen.networkevents.library.BusWrapper;
import com.github.pwittchen.networkevents.library.ConnectivityStatus;
import com.github.pwittchen.networkevents.library.NetworkEvents;
import com.github.pwittchen.networkevents.library.event.ConnectivityChanged;
import com.techery.spares.utils.ValidationUtils;
import com.worldventures.dreamtrips.core.api.AuthRetryPolicy;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.auth.api.command.LoginCommand;
import com.worldventures.dreamtrips.modules.auth.service.LoginInteractor;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.AuthorizedDataManager;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.ClearDirectoryDelegate;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.SessionAbsentException;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

import static com.github.pwittchen.networkevents.library.ConnectivityStatus.MOBILE_CONNECTED;
import static com.github.pwittchen.networkevents.library.ConnectivityStatus.WIFI_CONNECTED;
import static com.github.pwittchen.networkevents.library.ConnectivityStatus.WIFI_CONNECTED_HAS_INTERNET;
import static com.worldventures.dreamtrips.util.ValidationUtils.isPasswordValid;
import static com.worldventures.dreamtrips.util.ValidationUtils.isUsernameValid;

public class LaunchActivityPresenter extends ActivityPresenter<LaunchActivityPresenter.View> {

    @Inject SnappyRepository snappyRepository;
    @Inject ClearDirectoryDelegate clearTemporaryDirectoryDelegate;
    @Inject DrawableUtil drawableUtil;
    @Inject SnappyRepository db;
    @Inject DtlLocationInteractor dtlLocationInteractor;
    @Inject AuthorizedDataManager authorizedDataManager;
    @Inject LoginInteractor loginInteractor;

    private AuthorizedDataManager.AuthDataSubscriber authDataSubscriber;
    private NetworkEvents networkEvents;

    @Override
    public void dropView() {
        super.dropView();
        release();
    }

    @Override
    protected boolean canShowTermsDialog() {
        return false;
    }

    public void initDtl() {
        db.cleanLastSelectedOffersOnlyToggle();
        db.cleanLastMapCameraPosition();
        dtlLocationInteractor.locationPipe().send(DtlLocationCommand.change(DtlLocation.UNDEFINED));
    }

    @NonNull
    private BusWrapper getGreenRobotBusWrapper(final EventBus bus) {
        return new BusWrapper() {
            @Override
            public void register(Object object) {
                bus.register(object);
            }

            @Override
            public void unregister(Object object) {
                bus.unregister(object);
            }

            @Override
            public void post(Object event) {
                bus.post(event);
            }
        };
    }

    public void onEvent(ConnectivityChanged event) {
        ConnectivityStatus status = event.getConnectivityStatus();
        boolean internetConnected = status == MOBILE_CONNECTED || status == WIFI_CONNECTED_HAS_INTERNET || status == WIFI_CONNECTED;
        if (internetConnected) {
            authorizedDataManager.updateData(getAuthDataSubscriber());
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

        loginInteractor.loginActionPipe()
                .createObservable(new LoginCommand(username, userPassword))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindView())
                .subscribe(new ActionStateSubscriber<LoginCommand>()
                        .onStart(loginCommand -> view.showLoginProgress())
                        .onSuccess(loginCommand -> {
                            User user = loginCommand.getResult().getUser();
                            TrackingHelper.login(user.getEmail());
                            TrackingHelper.setUserId(Integer.toString(user.getId()));
                            view.openSplash();
                        })
                        .onFail((loginCommand, throwable) -> {
                            TrackingHelper.loginError();
                            view.alertLogin(loginCommand.getErrorMessage());
                        }));

    }

    public void splashModeStart() {
        clearTemporaryDirectoryDelegate.clearTemporaryDirectory();
        drawableUtil.removeCacheImages();
        BusWrapper busWrapper = getGreenRobotBusWrapper(eventBus);
        networkEvents = new NetworkEvents(context, busWrapper).enableWifiScan();
        networkEvents.register();

        startPreloadChain();
    }

    public void splashModeEnd() {
        release();
    }

    public void release() {
        if (networkEvents != null) networkEvents.unregister();
        if (authorizedDataManager != null) authorizedDataManager.unsubscribe();
    }

    public void startPreloadChain() {
        authorizedDataManager.updateData(getAuthDataSubscriber());
    }

    private AuthorizedDataManager.AuthDataSubscriber getAuthDataSubscriber() {
        if (authDataSubscriber == null || authDataSubscriber.isUnsubscribed()) {
            authDataSubscriber = new AuthorizedDataManager.AuthDataSubscriber()
                    .onStart(this::onAuthStart)
                    .onSuccess(this::onAuthSuccess)
                    .onFail(this::onAuthFail);
        }
        return authDataSubscriber;
    }

    private void onAuthStart() {
        if (view != null) view.configurationStarted();
    }

    private void onAuthSuccess() {
        if (view != null) view.openMain();
    }

    private void onAuthFail(Throwable throwable) {
        if (view == null) return;

        if (throwable instanceof SessionAbsentException || AuthRetryPolicy.isLoginError(throwable)) {
            view.openLogin();
        } else {
            view.informUser(new HumaneErrorTextFactory().create(throwable));
            view.configurationFailed();
        }
    }

    public interface View extends ActivityPresenter.View, ApiErrorView {

        void configurationFailed();

        void configurationStarted();

        void openLogin();

        void openSplash();

        void openMain();

        void alertLogin(String message);

        void showLoginProgress();

        void showLocalErrors(int userNameError, int passwordError);

        String getUsername();

        String getUserPassword();
    }
}
