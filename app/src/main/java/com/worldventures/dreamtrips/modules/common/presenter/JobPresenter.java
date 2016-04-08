package com.worldventures.dreamtrips.modules.common.presenter;


import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;

import rx.Observable;
import techery.io.library.Job;
import techery.io.library.JobCacheWiper;
import techery.io.library.JobExecutor;
import techery.io.library.JobSubscriber;

public class JobPresenter<VT extends RxView> extends Presenter<VT> {

    ///////////////////////////////////////////////////////////////////////////
    // Helpers - will go somewhere else
    ///////////////////////////////////////////////////////////////////////////

    public <T> JobSubscriber<T> bindJob(JobExecutor<T> executor) {
        return bindJobObservable(executor.connect());
    }

    public <T> JobSubscriber<T> bindJobCached(JobExecutor<T> executor) {
        return bindJobObservable(executor.connectWithCache().compose(new JobCacheWiper<>(executor)));
    }

    public <T> JobSubscriber<T> bindJobPersistantCached(JobExecutor<T> executor) {
        return bindJobObservable(executor.connectWithCache());
    }

    public <T> JobSubscriber<T> bindJobObservable(Observable<Job<T>> observable) {
        JobSubscriber<T> subscriber = new JobSubscriber<>();
        view.bind(observable
                .compose(new IoToMainComposer<>())
        ).subscribe(subscriber);
        return subscriber;
    }
}
