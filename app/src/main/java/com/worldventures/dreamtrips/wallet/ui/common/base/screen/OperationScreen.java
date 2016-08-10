package com.worldventures.dreamtrips.wallet.ui.common.base.screen;

import android.content.Context;
import android.support.annotation.Nullable;

import rx.functions.Action1;

public interface OperationScreen<T> {
    void showProgress(String msg);

    void hideProgress();

    void notifyError(String msg, @Nullable Action1<T> action);

    void showSuccess(String msg, @Nullable Action1<T> action);

    Context context();
}