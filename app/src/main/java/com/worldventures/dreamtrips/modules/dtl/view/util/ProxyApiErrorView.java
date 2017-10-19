package com.worldventures.dreamtrips.modules.dtl.view.util;


import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.modules.common.view.InformView;
import com.worldventures.dreamtrips.modules.dtl.view.util.DtlApiErrorViewAdapter.ApiErrorView;

import rx.functions.Action0;

public class ProxyApiErrorView implements ApiErrorView {

   final InformView informView;
   final Action0 action;

   public ProxyApiErrorView(InformView informView, Action0 action) {
      this.informView = informView;
      this.action = action;
   }

   @Override
   public void informUser(@StringRes int message) {
      informView.informUser(message);
   }

   @Override
   public void informUser(String message) {
      informView.informUser(message);
   }

   @Override
   public void onApiError() {
      action.call();
   }
}
