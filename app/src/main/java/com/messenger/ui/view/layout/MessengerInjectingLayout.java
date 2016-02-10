package com.messenger.ui.view.layout;

import android.content.Context;
import android.util.AttributeSet;

import com.messenger.ui.presenter.MessengerPresenter;
import com.techery.spares.module.Injector;

public abstract class MessengerInjectingLayout<V extends MessengerScreen, P extends MessengerPresenter<V, ?>>
        extends MessengerLinearLayout<V, P> {

    protected Injector injector;

    public MessengerInjectingLayout(Context context) {
        super(context);
    }

    public MessengerInjectingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
    }
}
