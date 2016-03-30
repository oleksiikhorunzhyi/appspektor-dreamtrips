package com.worldventures.dreamtrips.modules.dtl_flow.presenter;

import android.os.Parcelable;

import com.messenger.ui.presenter.BaseViewStateMvpPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.screen.FlowScreen;

public abstract class FlowPresenterImpl<V extends FlowScreen, S extends Parcelable>
        extends BaseViewStateMvpPresenter<V, S> implements FlowPresenter<V, S> {
}
