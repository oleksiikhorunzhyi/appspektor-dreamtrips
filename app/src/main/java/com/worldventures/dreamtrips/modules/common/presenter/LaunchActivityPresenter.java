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
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

import static com.github.pwittchen.networkevents.library.ConnectivityStatus.MOBILE_CONNECTED;
import static com.github.pwittchen.networkevents.library.ConnectivityStatus.WIFI_CONNECTED;
import static com.github.pwittchen.networkevents.library.ConnectivityStatus.WIFI_CONNECTED_HAS_INTERNET;


public class LaunchActivityPresenter extends ActivityPresenter<LaunchActivityPresenter.View> {

    private NetworkEvents networkEvents;

    @Inject
    SnappyRepository snappyRepository;
    @Inject
    ClearDirectoryDelegate clearTemporaryDirectoryDelegate;
    @Inject
    DrawableUtil drawableUtil;
    @Inject
    SnappyRepository db;
    @Inject
    DtlLocationManager locationManager;
    @Inject
    AuthorizedDataManager authorizedDataManager;
    AuthorizedDataManager.AuthDataSubscriber authDataSubscriber;

    private AuthorizedDataManager.AuthDataSubscriber getAuthDataSubscriber() {
        if (authDataSubscriber == null || authDataSubscriber.isUnsubscribed()) {
            authDataSubscriber = new AuthorizedDataManager.AuthDataSubscriber()
                    .onStart(() -> view.configurationStarted())
                    .onSuccess(() -> view.openMain())
                    .onFail(throwable -> {
                        if (throwable instanceof SessionAbsentException || AuthRetryPolicy.isLoginError(throwable)) {
                            view.openLogin();
                        } else {
                            view.informUser(new HumaneErrorTextFactory().create(throwable));
                            view.configurationFailed();
                        }
                    });
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
        snappyRepository.removeAllBucketItemPhotoCreations();
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
    }

    public void initDtl() {
        db.cleanLastSelectedOffersOnlyToggle();
        db.cleanLastMapCameraPosition();
        locationManager.cleanLocation();
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

    public interface View extends ActivityPresenter.View, ApiErrorView {
        void configurationFailed();

        void configurationStarted();

        void openLogin();

        void openMain();
    }
}
