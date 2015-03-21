package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;

/**
 * Created by 1 on 06.02.15.
 */
public class EnrollActivityPresenter extends BasePresenter<BasePresenter.View> {

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
