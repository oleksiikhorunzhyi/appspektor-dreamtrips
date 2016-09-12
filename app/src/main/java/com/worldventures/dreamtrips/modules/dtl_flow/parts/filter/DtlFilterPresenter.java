package com.worldventures.dreamtrips.modules.dtl_flow.parts.filter;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;

public interface DtlFilterPresenter extends MvpPresenter<FilterView> {

   void apply();

   void resetAll();

   void onDrawerOpened();

   void onDrawerClosed();
}
