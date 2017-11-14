package com.worldventures.wallet.ui.common.helper2.error;

import org.jetbrains.annotations.Nullable;

import io.techery.janet.operationsubscriber.view.ErrorView;

public interface ErrorViewProvider<T> {

   Class<? extends Throwable> forThrowable();

   @Nullable
   ErrorView<T> create(T t, @Nullable Throwable parentThrowable, Throwable throwable);
}
