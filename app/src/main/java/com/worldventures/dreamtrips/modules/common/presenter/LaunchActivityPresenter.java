package com.worldventures.dreamtrips.modules.common.presenter;


import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.preference.LocalesHolder;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.preference.StaticPageHolder;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.LegacyFeatureFactory;
import com.worldventures.dreamtrips.modules.common.api.GetLocaleQuery;
import com.worldventures.dreamtrips.modules.common.api.GlobalConfigQuery;
import com.worldventures.dreamtrips.modules.common.api.StaticPagesQuery;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;
import com.worldventures.dreamtrips.modules.common.model.AvailableLocale;
import com.worldventures.dreamtrips.modules.common.model.ServerStatus;
import com.worldventures.dreamtrips.modules.common.model.StaticPageConfig;
import com.worldventures.dreamtrips.modules.trips.api.GetActivitiesAndRegionsQuery;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.PickImageDialog;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import timber.log.Timber;

public class LaunchActivityPresenter extends Presenter<Presenter.View> {

    @Inject
    LocalesHolder localeStorage;

    @Inject
    StaticPageHolder staticPageHolder;

    @Inject
    SnappyRepository snappyRepository;

    @Inject
    Prefs prefs;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        GetLocaleQuery getLocaleQuery = new GetLocaleQuery();
        doRequest(getLocaleQuery, this::onLocaleSuccess);
        //clearTempDirectory();
    }

    private void clearTempDirectory() {
        File directory = new File(com.kbeanie.imagechooser.api.FileUtils.getDirectory(PickImageDialog.FOLDERNAME));
        if (directory.exists()) {
            try {
                FileUtils.deleteDirectory(directory);
            } catch (IOException e) {
                Timber.e(e, "Problem with remove temp image directory");
            }
        }
    }

    private void onLocaleSuccess(ArrayList<AvailableLocale> locales) {
        localeStorage.put(locales);
        loadStaticPagesContent();
    }

    private void loadStaticPagesContent() {
        Locale locale = getLocale();
        StaticPagesQuery staticPagesQuery = new StaticPagesQuery(locale.getCountry().toUpperCase(locale),
                locale.getLanguage().toUpperCase(locale));
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
        if (DreamSpiceManager.isCredentialExist(appSessionHolder)
                && prefs.getBoolean(Prefs.TERMS_ACCEPTED)) {
            UserSession userSession = appSessionHolder.get().get();
            if (userSession.getFeatures() == null ||
                    userSession.getFeatures().isEmpty()) {
                List<Feature> legacyFeatures = new LegacyFeatureFactory(userSession.getUser()).create();
                userSession.setFeatures(legacyFeatures);
                appSessionHolder.put(userSession);
            }
            activityRouter.openMain();
        } else {
            activityRouter.openLogin();
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

}
