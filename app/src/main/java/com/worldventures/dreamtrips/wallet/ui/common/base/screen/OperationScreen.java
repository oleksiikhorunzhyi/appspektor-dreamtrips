package com.worldventures.dreamtrips.wallet.ui.common.base.screen;

import android.content.Context;
import android.support.annotation.Nullable;

import rx.functions.Action1;

public interface OperationScreen<T> extends ErrorScreen<T> {

   void showProgress();

   void hideProgress();

   Context context();
}