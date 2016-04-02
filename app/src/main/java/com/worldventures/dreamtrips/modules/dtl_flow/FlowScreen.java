package com.worldventures.dreamtrips.modules.dtl_flow;

import android.support.annotation.StringRes;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;

public interface FlowScreen extends MvpView, ApiErrorView, Presenter.TabletAnalytic {

    void informUser(@StringRes int stringResId);

    void informUser(String message);
}
