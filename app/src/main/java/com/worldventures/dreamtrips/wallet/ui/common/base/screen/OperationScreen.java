package com.worldventures.dreamtrips.wallet.ui.common.base.screen;

import android.content.Context;
import android.support.annotation.Nullable;

public interface OperationScreen<T> extends ErrorScreen<T> {

   void showProgress(@Nullable String text);

   void hideProgress();

   Context context();
}