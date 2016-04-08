package com.messenger.ui.presenter;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;

import icepick.Icepick;
import icepick.State;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

public abstract class BaseViewStateMvpPresenter<V extends MvpView, S extends Parcelable> extends MvpBasePresenter<V>
        implements ViewStateMvpPresenter<V, S> {

    @State S state;

    PublishSubject<Void> detachStopper = PublishSubject.create();

    PublishSubject<Void> visibilityStopper = PublishSubject.create();


    @Override public void onSaveInstanceState(Bundle bundle) {
        Icepick.saveInstanceState(this, bundle);
    }

    @Override public void onRestoreInstanceState(Bundle instanceState) {
        Icepick.restoreInstanceState(this, instanceState);
        applyViewState();
    }

    @Override
    public void onAttachedToWindow() {

    }

    @Override
    public void onDetachedFromWindow() {

    }

    @Override
    public void onVisibilityChanged(int visibility) {
        if (visibility == View.GONE) visibilityStopper.onNext(null);
    }

    @Override
    public void detachView(boolean retainInstance) {
        detachStopper.onNext(null);
        super.detachView(retainInstance);
    }

    protected <T> Observable.Transformer<T, T> bindVisibility() {
        return input -> input.takeUntil(visibilityStopper);
    }

    protected <T> Observable.Transformer<T, T> bindView() {
        return input -> input.takeUntil(detachStopper);
    }

    @Override public S getViewState() {
        return state;
    }

    ///////////////////////////////////////////////////
    /////// Helpers
    //////////////////////////////////////////////////

    protected <T> Observable.Transformer<T, T> bindVisibilityIoToMainComposer() {
        return input -> input
                .compose(new IoToMainComposer<>())
                .compose(bindVisibility());
    }

    protected <T> Observable.Transformer<T, T> bindViewIoToMainComposer() {
        return input -> input
                .compose(new IoToMainComposer<>())
                .compose(bindView());
    }
}
