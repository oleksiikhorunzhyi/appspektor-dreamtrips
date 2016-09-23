package com.worldventures.dreamtrips.wallet.ui.common.base.screen;

import android.content.Context;
import android.support.annotation.Nullable;

import rx.functions.Action1;

public interface OperationScreen<T> {
   void showProgress();

   void hideProgress();

   void showError(String msg, @Nullable Action1<T> action);

   Context context();
}