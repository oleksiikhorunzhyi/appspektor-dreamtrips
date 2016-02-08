package com.worldventures.dreamtrips.modules.common.presenter;


import android.support.annotation.NonNull;

import com.github.pwittchen.networkevents.library.BusWrapper;
import com.github.pwittchen.networkevents.library.ConnectivityStatus;
import com.github.pwittchen.networkevents.library.NetworkEvents;
import com.github.pwittchen.networkevents.library.event.ConnectivityChanged;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.preference.LocalesHolder;
import com.worldventures.dreamtrips.core.preference.StaticPageHolder;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.LegacyFeatureFactory;
import com.worldventures.dreamtrips.core.utils.FileUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.api.GetLocaleQuery;
import com.worldventures.dreamtrips.modules.common.api.GlobalConfigQuery;
import com.worldventures.dreamtrips.modules.common.api.StaticPagesQuery;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;
import com.worldventures.dreamtrips.modules.common.model.AvailableLocale;
import com.worldventures.dreamtrips.modules.common.model.ServerStatus;
import com.worldventures.dreamtrips.modules.common.model.StaticPageConfig;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.settings.api.GetSettingsQuery;
import com.worldventures.dreamtrips.modules.settings.model.SettingsHolder;
import com.worldventures.dreamtrips.modules.settings.util.SettingsFactory;
import com.worldventures.dreamtrips.modules.settings.util.SettingsManager;
import com.worldventures.dreamtrips.modules.trips.api.GetActivitiesAndRegionsQuery;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

import static com.github.pwittchen.networkevents.library.ConnectivityStatus.MOBILE_CONNECTED;
import static com.github.pwittchen.networkevents.library.ConnectivityStatus.WIFI_CONNECTED;
import static com.github.pwittchen.networkevents.library.ConnectivityStatus.WIFI_CONNECTED_HAS_INTERNET;

public class LaunchActivityPresenter extends ActivityPresenter<LaunchActivityPresenter.View> {

    private BusWrapper busWrapper;
    private NetworkEvents networkEvents;

    @Inject
    LocalesHolder localeStorage;

    @Inject
    DtlLocationManager dtlLocationManager;

    @Inject
    StaticPageHolder staticPageHolder;

    @Inject
    SnappyRepository snappyRepository;

    @Inject
    Router router;

    private boolean requestInProgress = false;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        clearTempDirectory();
        busWrapper = getGreenRobotBusWrapper(eventBus);
        networkEvents = new NetworkEvents(context, busWrapper).enableWifiScan();
        networkEvents.register();

        startPreloadChain();

        // we should clean dtl location when app was relaunched
        dtlLocationManager.cleanLocation();
    }

    @Override
    public void dropView() {
        super.dropView();
        networkEvents.unregister();
    }

    public void startPreloadChain() {
        doRequest(new GetLocaleQuery(), this::onLocaleSuccess);
        view.configurationStarted();
        requestInProgress = true;

    }

    private void onLocaleSuccess(ArrayList<AvailableLocale> locales) {
        localeStorage.put(locales);
        UserSession userSession = appSessionHolder.get().isPresent() ? appSessionHolder.get().get() : null;
    /*    if (userSession != null && userSession.getApiToken() != null)
            loadSettings();
        else*/
            done();
    }

    private void loadSettings() {
        doRequest(new GetSettingsQuery(), this::onSettingsLoaded);
    }

    private void onSettingsLoaded(SettingsHolder settingsHolder) {
        snappyRepository.saveSettings(SettingsManager.merge(settingsHolder.getSettings(),
                SettingsFactory.createSettings()), true);
        loadStaticPagesContent();
    }

    private void loadStaticPagesContent() {
        Locale locale = getLocale();
        StaticPagesQuery staticPagesQuery = new StaticPagesQuery(locale.getCountry(), locale.getLanguage());
        doRequest(staticPagesQuery, this::onStaticPagesSuccess);
    }

    private void onStaticPagesSuccess(StaticPageConfig staticPageConfig) {
        staticPageHolder.put(staticPageConfig);
        loadGlobalConfig();
    }

    private void loadGlobalConfig() {
        GlobalConfigQuery.GetConfigRequest getConfigRequest = new GlobalConfigQuery.GetConfigRequest();
        doRequest(getConfigRequest, this::proccessAppConfig);
    }

    private void proccessAppConfig(AppConfig appConfig) {
        ServerStatus.Status serv = appConfig.getServerStatus().getProduction();
        String status = serv.getStatus();
        String message = serv.getMessage();

        if (!"up".equalsIgnoreCase(status)) {
            view.alert(message);
        } else {
            UserSession userSession;
            if (appSessionHolder.get().isPresent()) {
                userSession = appSessionHolder.get().get();
            } else {
                userSession = new UserSession();
            }

            userSession.setGlobalConfig(appConfig);
            appSessionHolder.put(userSession);
            loadFiltersData();
        }
    }

    private void loadFiltersData() {
        doRequest(new GetActivitiesAndRegionsQuery(snappyRepository), (result) -> done());
    }

    private void done() {
        if (DreamSpiceManager.isCredentialExist(appSessionHolder)) {
            UserSession userSession = appSessionHolder.get().get();
            if (userSession.getFeatures() == null ||
                    userSession.getFeatures().isEmpty()) {
                List<Feature> legacyFeatures = new LegacyFeatureFactory(userSession.getUser()).create();
                userSession.setFeatures(legacyFeatures);
                appSessionHolder.put(userSession);
            }

            TrackingHelper.setUserId(Integer.toString(userSession.getUser().getId()));
            activityRouter.openMain();
        } else {
            router.moveTo(Route.LOGIN, NavigationConfigBuilder.forActivity()
                    .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                    .build());
        }
        activityRouter.finish();
    }

    private Locale getLocale() {
        boolean contains = false;
        Locale localeCurrent = Locale.getDefault();
        Optional<ArrayList<AvailableLocale>> localesOptional = localeStorage.get();
        if (localesOptional.isPresent()) {
            List<AvailableLocale> availableLocales = localesOptional.get();
            contains = Queryable.from(availableLocales)
                    .any((availableLocale) ->
                                    localeCurrent.getCountry().equalsIgnoreCase(availableLocale.getCountry()) &&
                                            localeCurrent.getLanguage().equalsIgnoreCase(availableLocale.getLanguage())
                    );
        }
        return !contains ? Locale.US : localeCurrent;
    }

    public void onEvent(ConnectivityChanged event) {
        ConnectivityStatus status = event.getConnectivityStatus();
        boolean internetConnected = status == MOBILE_CONNECTED || status == WIFI_CONNECTED_HAS_INTERNET || status == WIFI_CONNECTED;
        if (internetConnected && !requestInProgress) {
            startPreloadChain();
        }
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        requestInProgress = false;
        view.configurationFailed();
    }

    @Override
    protected boolean canShowTermsDialog() {
        return false;
    }

    private void clearTempDirectory() {
        snappyRepository.removeAllUploadTasks();
        File directory = new File(com.kbeanie.imagechooser.api.FileUtils.getDirectory(PickImageDelegate.FOLDERNAME));
        if (!directory.exists()) return;
        try {
            FileUtils.cleanDirectory(context, directory);
        } catch (IOException e) {
            Timber.e(e, "Problem with remove temp image directory");
        }
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


    public interface View extends ActivityPresenter.View {
        void configurationFailed();

        void configurationStarted();
    }
}
