package com.worldventures.dreamtrips.modules.dtl_flow;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;

public interface DtlScreen extends MvpView, ApiErrorView, Presenter.TabletAnalytic {
}
