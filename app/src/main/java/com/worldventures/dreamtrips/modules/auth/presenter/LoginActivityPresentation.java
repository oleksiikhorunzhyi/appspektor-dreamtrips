package com.worldventures.dreamtrips.modules.auth.presenter;

import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresentation;


public class LoginActivityPresentation extends BasePresentation<BasePresentation.View> {

    public LoginActivityPresentation(View view) {
        super(view);
    }

    public void onCreate() {
        fragmentCompass.add(State.LOGIN);
    }
}
