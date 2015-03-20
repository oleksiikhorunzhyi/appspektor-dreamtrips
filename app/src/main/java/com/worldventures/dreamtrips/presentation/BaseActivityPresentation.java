package com.worldventures.dreamtrips.presentation;

import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.utils.events.ServerConfigError;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class BaseActivityPresentation<VT extends BasePresentation.View> extends BasePresentation<VT> {
    @Inject
    @Global
    EventBus eventBus;

    public BaseActivityPresentation(VT view) {
        super(view);
    }

    public void init() {
        super.init();
        eventBus.register(this);
    }

    public void onEventMainThread(ServerConfigError s) {
        view.alert(s.getError());
    }

}
