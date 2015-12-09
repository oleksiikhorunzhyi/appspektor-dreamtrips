package com.worldventures.dreamtrips.modules.dtl.store;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.RequestingPresenter;

import java.util.List;

import timber.log.Timber;

public abstract class RequestingCachingBaseStore {

    protected SnappyRepository db;
    protected RequestingPresenter requestingPresenter;

    public RequestingCachingBaseStore(SnappyRepository db) {
        this.db = db;
    }

    protected void checkState() {
        if (requestingPresenter == null)
            throw new IllegalStateException("You should set RequestingPresenter before loading anything");
    }

    protected void checkListeners(List... listenersLists) {
        for(List list : listenersLists) {
            if (list.isEmpty())
                Timber.w("Checking store listeners: no registered listener of desired type! Check your setup.");
        }
    }
}
