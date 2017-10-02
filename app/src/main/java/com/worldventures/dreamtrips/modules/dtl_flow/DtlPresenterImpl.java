package com.worldventures.dreamtrips.modules.dtl_flow;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.ui.presenter.BaseViewStateMvpPresenter;
import com.worldventures.core.service.analytics.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.dtl.view.util.DtlApiErrorViewAdapter;

import javax.inject.Inject;

public abstract class DtlPresenterImpl<V extends DtlScreen, S extends Parcelable> extends BaseViewStateMvpPresenter<V, S> implements DtlPresenter<V, S> {

   @Inject protected AnalyticsInteractor analyticsInteractor;
   @Inject protected DtlApiErrorViewAdapter apiErrorViewAdapter;

   protected Context context;

   public DtlPresenterImpl(Context context) {
      this.context = context;
   }

   public Context getContext() {
      return context;
   }

   @Override
   public int getToolbarMenuRes() {
      return 0;
   }

   @Override
   public void onToolbarMenuPrepared(Menu menu) {
   }

   @Override
   public boolean onToolbarMenuItemClick(MenuItem item) {
      return false;
   }

   @Override
   public void onNewViewState() {
   }

   @Override
   public void applyViewState() {
   }

   @Override
   @CallSuper
   public void onDetachedFromWindow() {
      apiErrorViewAdapter.dropView();
   }
}
