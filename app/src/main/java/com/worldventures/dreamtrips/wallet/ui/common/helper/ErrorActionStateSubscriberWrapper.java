package com.worldventures.dreamtrips.wallet.ui.common.helper;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.ui.common.base.screen.ErrorScreen;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;

public class ErrorActionStateSubscriberWrapper<T> {

   private final ErrorScreen view;
   @Nullable private Func1<Throwable, MessageActionHolder<T>> onFailFactory;
   @Nullable private Action1<T> onSuccessAction;
   @Nullable private Action1<T> onStartAction;
   @Nullable private Action2<T, Integer> onProgressAction;

   private ErrorActionStateSubscriberWrapper(ErrorScreen errorView) {
      this.view = errorView;
   }

   public static <T> ErrorActionStateSubscriberWrapper<T> forView(ErrorScreen view) {
      return new ErrorActionStateSubscriberWrapper<>(view);
   }

   public ErrorActionStateSubscriberWrapper<T> onSuccess(@Nullable Action1<T> onSuccess) {
      this.onSuccessAction = onSuccess;
      return this;
   }

   public ErrorActionStateSubscriberWrapper<T> onStart(@Nullable Action1<T> onStart) {
      this.onStartAction = onStart;
      return this;
   }

   public ErrorActionStateSubscriberWrapper<T> onProgress(@Nullable Action2<T, Integer> onProgress) {
      this.onProgressAction = onProgress;
      return this;
   }

   public ErrorActionStateSubscriberWrapper<T> onFail(String message, @Nullable Action1<T> onFail) {
      this.onFailFactory = throwable -> new MessageActionHolder<>(message, onFail);
      return this;
   }

   public ErrorActionStateSubscriberWrapper<T> onFail(String message) {
      return onFail(message, null);
   }

   public ErrorActionStateSubscriberWrapper<T> onFail(Func1<Throwable, MessageActionHolder<T>> factoryFailFunc) {
      this.onFailFactory = factoryFailFunc;
      return this;
   }

   public ActionStateSubscriber<T> wrap() {
      return new ActionStateSubscriber<T>()
            .onStart(t -> {
               if (onStartAction != null) onStartAction.call(t);
            })
            .onSuccess(t -> {
               if (onSuccessAction != null) onSuccessAction.call(t);
            })
            .onProgress((t, progress) -> {
               if (onProgressAction != null) onProgressAction.call(t, progress);
            })
            .onFail((t, throwable) -> {
               final MessageActionHolder<T> failHolder = onFailFactory != null ? onFailFactory.call(throwable) : null;
               if (failHolder == null) return;
               view.showError(failHolder.message.provide(t), failHolder.action == null ? null : o -> failHolder.action.call(t));
            });
   }

}
