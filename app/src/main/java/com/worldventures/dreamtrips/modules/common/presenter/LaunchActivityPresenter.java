package com.worldventures.dreamtrips.modules.common.presenter;


import com.techery.spares.storage.complex_objects.ComplexObjectStorage;
import com.worldventures.dreamtrips.modules.common.api.GetLocaleQuery;
import com.worldventures.dreamtrips.modules.common.model.AvailableLocale;

import java.util.ArrayList;

import javax.inject.Inject;

public class LaunchActivityPresenter extends Presenter<Presenter.View> {

    @Inject
    ComplexObjectStorage<ArrayList<AvailableLocale>> localeStorage;

    public LaunchActivityPresenter(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        GetLocaleQuery getLocaleQuery = new GetLocaleQuery();
        doRequest(getLocaleQuery, (locales) -> onSuccess(locales));
    }

    public void onSuccess(ArrayList<AvailableLocale> locales) {
        localeStorage.put(locales);

        if (isLogged()) {
            activityRouter.openMain();
        } else {
            activityRouter.openLogin();
        }

        activityRouter.finish();
    }

    private void saveLocales() {
    }

    public boolean isLogged() {
        return appSessionHolder.get().isPresent();
    }

}
