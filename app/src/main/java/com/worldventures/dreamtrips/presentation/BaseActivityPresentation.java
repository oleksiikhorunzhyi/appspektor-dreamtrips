package com.worldventures.dreamtrips.presentation;

import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.utils.busevents.ServerConfigError;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class BaseActivityPresentation<VT extends BasePresentation.View> extends BasePresentation<VT> {
    public BaseActivityPresentation(VT view) {
        super(view);
    }

    @Inject
    @Global
    EventBus eventBus;


    public void init() {
        super.init();
        eventBus.register(this);
    }

    public void onEventMainThread(ServerConfigError s) {
        view.alert(s.getError());
    }

}
