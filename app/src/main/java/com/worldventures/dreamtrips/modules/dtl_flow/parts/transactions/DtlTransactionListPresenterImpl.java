package com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions;

import android.content.Context;

import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public class DtlTransactionListPresenterImpl extends DtlPresenterImpl<DtlTransactionListScreen, ViewState.EMPTY> implements DtlTransactionListPresenter {

   public DtlTransactionListPresenterImpl(Context context, Injector injector) {
      super(context);
      injector.inject(this);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
   }

   @Override
   public void onDetachedFromWindow() {
      super.onDetachedFromWindow();
   }

   @Override
   public void onBackPressed() {

   }
}