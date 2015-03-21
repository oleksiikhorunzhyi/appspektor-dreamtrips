package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresentation;

/**
 * Created by 1 on 06.02.15.
 */
public class EnrollActivityPresentation extends BasePresentation<BasePresentation.View> {

    public EnrollActivityPresentation(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
    }

    public void onCreate() {
        fragmentCompass.add(State.ENROLL);
    }
}
