package com.worldventures.dreamtrips.core.utils.tracksystem;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public class AnalyticsInteractor {

    private final ActionPipe<BaseAnalyticsAction> sendAnalyticEventPipe;

    public AnalyticsInteractor(Janet janet) {
        sendAnalyticEventPipe = janet.createPipe(BaseAnalyticsAction.class, Schedulers.io());
    }

    public void send(BaseAnalyticsAction action, Scheduler subscribeOn) {
        sendAnalyticEventPipe.send(action, subscribeOn);
    }

    /**
     * Proxy method to write to analytics pipe <br />
     * <b>NOTE:</b> by default action will be executed in worked thread. <br />
     * To execute in main thread use
     * {@link AnalyticsInteractor#send(BaseAnalyticsAction, Scheduler)} method
     * and pass corresponding scheduler
     * @param action action to execute
     */
    public void send(BaseAnalyticsAction action) {
        sendAnalyticEventPipe.send(action);
    }
}
