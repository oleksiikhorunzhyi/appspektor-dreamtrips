package com.worldventures.dreamtrips.modules.dtl.store;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.presenter.RequestingPresenter;

import java.util.List;

import timber.log.Timber;

public abstract class RequestingCachingBaseStore {

    protected SnappyRepository db;
    protected RequestingPresenter requestingPresenter;

    public RequestingCachingBaseStore(SnappyRepository db) {
        this.db = db;
    }

    /**
     * Set RequestingPresenter in {@link Presenter#onInjected()}
     *
     * @param requestingPresenter {@link RequestingPresenter} presenter to execute Requests
     */
    public void setRequestingPresenter(RequestingPresenter requestingPresenter) {
        this.requestingPresenter = requestingPresenter;
    }

    /**
     * Detach requesting presenter instance to prevent leak. To be used in {@link Presenter#dropView()}
     */
    public void detachRequestingPresenter() {
        this.requestingPresenter = null;
    }

    /**
     * Toggle before doing any request.
     *
     * @throws IllegalStateException if requestingPresenter is null.<br />
     */
    protected void checkState() {
        if (requestingPresenter == null)
            throw new IllegalStateException("You should set RequestingPresenter before loading anything");
    }

    /**
     * Logs a warning if no listener of desired type attached.<br />
     * Helps to figure out possible fucked-up setup faster.
     *
     * @param listenersLists all lists of listeners that are to be triggered
     */
    protected void checkListeners(List... listenersLists) {
        for(List list : listenersLists) {
            if (list.isEmpty())
                Timber.w("Checking store listeners: no registered listener of desired type! Check your setup.");
        }
    }
}
