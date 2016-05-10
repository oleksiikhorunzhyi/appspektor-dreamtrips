package com.messenger.ui.module;

import com.trello.rxlifecycle.RxLifecycle;

import rx.Observable;

public abstract class ModulePresenterImpl<V extends ModuleView> implements ModulePresenter<V> {

    private V view;

    public ModulePresenterImpl(V view) {
        this.view = view;
    }

    protected <T> Observable.Transformer<T, T> bindView() {
        return input -> input
                .compose(RxLifecycle.bindView(view.getParentView()));
    }

    @Override
    public V getView() {
        return view;
    }

    @Override
    public void onParentViewAttachedToWindow() {
    }

    @Override
    public void onParentViewDetachedFromWindow() {
    }
}
