package com.messenger.ui.module;

public interface ModuleStatefulPresenter<V extends ModuleView, S> extends ModulePresenter<V> {

    void applyState(S state);

    S getState();
}
