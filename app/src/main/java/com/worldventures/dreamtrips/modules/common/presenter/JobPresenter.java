package com.worldventures.dreamtrips.modules.common.presenter;


import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;

import techery.io.library.JobCacheWiper;
import techery.io.library.JobExecutor;
import techery.io.library.JobSubscriber;

public class JobPresenter<VT extends RxView> extends Presenter<VT> {

    ///////////////////////////////////////////////////////////////////////////
    // Helpers - will go somewhere else
    ///////////////////////////////////////////////////////////////////////////

    public <T> JobSubscriber<T> bindJob(JobExecutor<T> executor) {
        JobSubscriber<T> subscriber = new JobSubscriber<>();
        view.bind(executor.connect().compose(IoToMainComposer.get())).subscribe(subscriber);
        return subscriber;
    }

    public <T> JobSubscriber<T> bindJobCached(JobExecutor<T> executor) {
        JobSubscriber<T> subscriber = new JobSubscriber<>();
        view.bind(executor.connectWithCache()
                .compose(IoToMainComposer.get())
                .compose(new JobCacheWiper<>(executor))
        ).subscribe(subscriber);
        return subscriber;
    }
}