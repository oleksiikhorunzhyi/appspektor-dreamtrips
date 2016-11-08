package com.worldventures.dreamtrips.wallet.ui.common.base.screen;

import android.support.annotation.Nullable;

import rx.functions.Action1;

public interface ErrorScreen<T> {

   void showError(String msg, @Nullable Action1<T> action);
}
