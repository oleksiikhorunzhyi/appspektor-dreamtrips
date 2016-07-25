package com.worldventures.dreamtrips.modules.common.presenter;

import android.support.annotation.NonNull;

import com.github.pwittchen.networkevents.library.BusWrapper;
import com.github.pwittchen.networkevents.library.ConnectivityStatus;
import com.github.pwittchen.networkevents.library.NetworkEvents;
import com.github.pwittchen.networkevents.library.event.ConnectivityChanged;
import com.worldventures.dreamtrips.core.api.AuthRetryPolicy;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
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

import static com.github.pwittchen.networkevents.library.ConnectivityStatus.MOBILE_CONNECTED;
import static com.github.pwittchen.networkevents.library.ConnectivityStatus.WIFI_CONNECTED;
import static com.github.pwittchen.networkevents.library.ConnectivityStatus.WIFI_CONNECTED_HAS_INTERNET;

public class LaunchActivityPresenter extends ActivityPresenter<LaunchActivityPresenter.View> {

    @Inject
    SnappyRepository snappyRepository;
    @Inject
    ClearDirectoryDelegate clearTemporaryDirectoryDelegate;
    @Inject
    DrawableUtil drawableUtil;
    @Inject
    SnappyRepository db;
    @Inject
    DtlLocationInteractor dtlLocationInteractor;
    @Inject
    AuthorizedDataManager authorizedDataManager;

    private AuthorizedDataManager.AuthDataSubscriber authDataSubscriber;

    private NetworkEvents networkEvents;

    private AuthorizedDataManager.AuthDataSubscriber getAuthDataSubscriber() {
        if (authDataSubscriber == null || authDataSubscriber.isUnsubscribed()) {
            authDataSubscriber = new AuthorizedDataManager.AuthDataSubscriber()
                    .onStart(this::onAuthStart)
                    .onSuccess(this::onAuthSuccess)
                    .onFail(this::onAuthFail);
        }
        return authDataSubscriber;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        clearTemporaryDirectoryDelegate.clearTemporaryDirectory();
        drawableUtil.removeCacheImages();
        BusWrapper busWrapper = getGreenRobotBusWrapper(eventBus);
        networkEvents = new NetworkEvents(context, busWrapper).enableWifiScan();
        networkEvents.register();

        startPreloadChain();
    }

    @Override
    public void onResume() {
        super.onResume();
        authorizedDataManager.updateData(getAuthDataSubscriber());
    }

    @Override
    public void dropView() {
        super.dropView();
        networkEvents.unregister();
        if (authorizedDataManager != null) authorizedDataManager.unsubscribe();
    }

    public void initDtl() {
        db.cleanLastSelectedOffersOnlyToggle();
        db.cleanLastMapCameraPosition();
        dtlLocationInteractor.locationPipe().send(DtlLocationCommand.change(DtlLocation.UNDEFINED));
    }

    public void onEvent(ConnectivityChanged event) {
        ConnectivityStatus status = event.getConnectivityStatus();
        boolean internetConnected = status == MOBILE_CONNECTED || status == WIFI_CONNECTED_HAS_INTERNET || status == WIFI_CONNECTED;
        if (internetConnected) {
            authorizedDataManager.updateData(getAuthDataSubscriber());
        }
    }

    @Override
    protected boolean canShowTermsDialog() {
        return false;
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

    public void startPreloadChain() {
        authorizedDataManager.updateData(getAuthDataSubscriber());
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

        void openMain();
    }
}
