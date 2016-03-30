package com.worldventures.dreamtrips.modules.dtl_flow;

import android.content.Context;
import android.util.AttributeSet;

import com.messenger.ui.view.layout.BaseViewStateLinearLayout;
import com.techery.spares.module.Injector;

public abstract class InjectingLayout<V extends FlowScreen, P extends FlowPresenter<V, ?>>
        extends BaseViewStateLinearLayout<V, P> implements FlowScreen {

    protected Injector injector;

    public InjectingLayout(Context context) {
        super(context);
    }

    public InjectingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
    }
}
