package com.worldventures.dreamtrips.modules.common.presenter;

import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.utils.events.ServerConfigError;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class BaseActivityPresenter<VT extends BasePresenter.View> extends BasePresenter<VT> {
    @Inject
    @Global
    EventBus eventBus;

    public BaseActivityPresenter(VT view) {
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