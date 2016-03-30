package com.worldventures.dreamtrips.modules.dtl_flow.layout;

import android.content.Context;
import android.util.AttributeSet;

import com.messenger.ui.view.layout.BaseViewStateLinearLayout;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.dtl_flow.presenter.FlowPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.screen.FlowScreen;

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
