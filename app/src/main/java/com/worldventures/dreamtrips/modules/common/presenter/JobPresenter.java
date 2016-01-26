package com.worldventures.dreamtrips.modules.common.presenter;

import com.worldventures.dreamtrips.core.rx.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.RxView;

import techery.io.library.JobCacheWiper;
import techery.io.library.JobExecutor;
import techery.io.library.JobSubscriber;

public class JobPresenter<VT extends RxView> extends Presenter<VT> {

    ///////////////////////////////////////////////////////////////////////////
    // Helpers - will go somewhere else
    ///////////////////////////////////////////////////////////////////////////

    public <T> JobSubscriber<T> bindJob(JobExecutor<T> executor) {
        JobSubscriber<T> subscriber = new JobSubscriber<>();
        view.bind(executor.connect()
                        .compose(new IoToMainComposer<>())
        ).subscribe(subscriber);
        return subscriber;
    }

    public <T> JobSubscriber<T> bindJobCached(JobExecutor<T> executor) {
        JobSubscriber<T> subscriber = new JobSubscriber<>();
        view.bind(executor.connectWithCache()
                        .compose(new IoToMainComposer<>())
                        .compose(new JobCacheWiper<>(executor))
        ).subscribe(subscriber);
        return subscriber;
    }
}
