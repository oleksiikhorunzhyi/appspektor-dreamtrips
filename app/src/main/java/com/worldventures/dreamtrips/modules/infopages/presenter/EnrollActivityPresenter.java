package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public class EnrollActivityPresenter extends Presenter<Presenter.View> {

    public void onCreate() {
        fragmentCompass.add(Route.ENROLL);
    }
}
