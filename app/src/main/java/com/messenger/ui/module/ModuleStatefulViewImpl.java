package com.messenger.ui.module;

import android.os.Parcelable;
import android.view.View;

public abstract class ModuleStatefulViewImpl<P extends ModuleStatefulPresenter> extends ModuleViewImpl<P>
        implements ModuleStatefulView<P> {

    private Parcelable lastInstanceState;

    public ModuleStatefulViewImpl(View parentView) {
        super(parentView);
    }

    @Override
    public void onSaveInstanceState(Parcelable parcelable) {
        if (getPresenter() != null) getPresenter().onSaveInstanceState(parcelable);
    }

    @Override
    public void onRestoreInstanceState(Parcelable parcelable) {
        // save instance state to forward to presenter later on onAttachedToWindow() callback
        // to make sure everything is properly initialized on presenter's side
        lastInstanceState = parcelable;
    }

    @Override
    protected void onParentViewAttachedToWindow() {
        super.onParentViewAttachedToWindow();
        if (lastInstanceState != null && getPresenter() != null) {
            getPresenter().onRestoreInstanceState(lastInstanceState);
        }
    }
}
