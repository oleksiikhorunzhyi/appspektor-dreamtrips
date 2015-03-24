package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

/**
 *  1 on 06.02.15.
 */
public class EnrollActivityPresenter extends Presenter<Presenter.View> {

    public EnrollActivityPresenter(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
    }

    public void onCreate() {
        fragmentCompass.add(Route.ENROLL);
    }
}
