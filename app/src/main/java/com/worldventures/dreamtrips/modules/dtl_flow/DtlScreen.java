package com.worldventures.dreamtrips.modules.dtl_flow;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.BlockingProgressView;
import com.worldventures.dreamtrips.modules.common.view.InformView;

public interface DtlScreen extends MvpView, InformView, BlockingProgressView, Presenter.TabletAnalytic {}
