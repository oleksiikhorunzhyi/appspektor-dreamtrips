package com.worldventures.dreamtrips.modules.common.presenter;


import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.preference.LocalesHolder;
import com.worldventures.dreamtrips.core.preference.StaticPageHolder;
import com.worldventures.dreamtrips.modules.common.api.GetLocaleQuery;
import com.worldventures.dreamtrips.modules.common.api.StaticPagesQuery;
import com.worldventures.dreamtrips.modules.common.model.AvailableLocale;
import com.worldventures.dreamtrips.modules.common.model.StaticPageConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class LaunchActivityPresenter extends Presenter<Presenter.View> {

    @Inject
    LocalesHolder localeStorage;

    @Inject
    StaticPageHolder staticPageHolder;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        GetLocaleQuery getLocaleQuery = new GetLocaleQuery();
        doRequest(getLocaleQuery, (locales) -> {
            onLocaleSuccess(locales);
        });
    }

    private boolean isLogged() {
        return appSessionHolder.get().isPresent();
    }

    private void loadStaticPagesContent() {
        Locale locale = getLocale();
        StaticPagesQuery staticPagesQuery = new StaticPagesQuery(locale.getCountry().toUpperCase(locale),
                locale.getLanguage().toUpperCase(locale));
        doRequest(staticPagesQuery, staticPageConfig -> onStaticPagesSuccess(staticPageConfig));
    }

    private Locale getLocale() {
        boolean contains = false;
        Locale localeCurrent = Locale.getDefault();
        Optional<ArrayList<AvailableLocale>> localesOptional = localeStorage.get();
        if (localesOptional.isPresent()) {
            List<AvailableLocale> availableLocales = localesOptional.get();
            contains = Queryable.from(availableLocales)
                    .any((availableLocale) -> {
                        return localeCurrent.getCountry().equalsIgnoreCase(availableLocale.getCountry()) &&
                                localeCurrent.getLanguage().equalsIgnoreCase(availableLocale.getLanguage());
                    });
        }
        return !contains ? Locale.US : localeCurrent;
    }


    private void onLocaleSuccess(ArrayList<AvailableLocale> locales) {
        localeStorage.put(locales);
        loadStaticPagesContent();
    }

    private void onStaticPagesSuccess(StaticPageConfig staticPageConfig) {
        staticPageHolder.put(staticPageConfig);
        done();
    }

    private void done() {
        if (isLogged()) {
            activityRouter.openMain();
        } else {
            activityRouter.openLogin();
        }
        activityRouter.finish();
    }
}
