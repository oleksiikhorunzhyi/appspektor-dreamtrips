package com.messenger.ui.module;

import android.content.Context;
import android.view.View;

public abstract class ModuleViewImpl<P extends ModulePresenter> implements ModuleView<P> {

    private View parentView;

    private P presenter;

    private View.OnAttachStateChangeListener parentAttachedStateListener
            = new View.OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View v) {
            onParentViewAttachedToWindow();
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            onParentViewDetachedFromWindow();
        }
    };

    public ModuleViewImpl(View parentView) {
        this.parentView = parentView;
        parentView.addOnAttachStateChangeListener(parentAttachedStateListener);
    }

    public View getParentView() {
        return parentView;
    }

    protected void onParentViewAttachedToWindow() {
        presenter.onParentViewAttachedToWindow();
    }

    protected void onParentViewDetachedFromWindow() {
        presenter.onParentViewDetachedFromWindow();
    }

    @Override
    public P getPresenter() {
        return presenter;
    }

    @Override
    public void setPresenter(P presenter) {
        this.presenter = presenter;
    }

    public Context getContext() {
        return parentView.getContext();
    }
}
