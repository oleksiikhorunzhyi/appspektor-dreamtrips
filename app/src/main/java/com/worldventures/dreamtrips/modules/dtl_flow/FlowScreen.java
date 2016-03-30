package com.worldventures.dreamtrips.modules.dtl_flow;

import android.support.annotation.StringRes;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface FlowScreen extends MvpView {

    void informUser(@StringRes int stringResId);

    void informUser(String message);
}
