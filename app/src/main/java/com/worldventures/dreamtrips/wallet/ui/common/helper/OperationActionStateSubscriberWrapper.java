package com.worldventures.dreamtrips.wallet.ui.common.helper;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;

@Deprecated
public final class OperationActionStateSubscriberWrapper<T> {

   private OperationScreen view;

   private Func1<Throwable, MessageActionHolder<T>> onFailFactory;
   private Action2<T, Integer> onProgress;
   private Action1<T> onSuccessAction;
   private @Nullable String progressText = null;

   private OperationActionStateSubscriberWrapper(OperationScreen view) {
      this.view = view;
   }

   public static <T> OperationActionStateSubscriberWrapper<T> forView(OperationScreen view) {
      return new OperationActionStateSubscriberWrapper<>(view);
   }

   public OperationActionStateSubscriberWrapper<T> onStart(String message) {
      this.progressText = message;
      return this;
   }

   public OperationActionStateSubscriberWrapper<T> onSuccess(@Nullable Action1<T> onSuccess) {
      this.onSuccessAction = onSuccess;
      return this;
   }

   public OperationActionStateSubscriberWrapper<T> onFail(String message, @Nullable Action1<T> onFail) {
      this.onFailFactory = throwable -> new MessageActionHolder<>(message, onFail);
      return this;
   }

   public OperationActionStateSubscriberWrapper<T> onFail(String message) {
      return onFail(message, null);
   }

   public OperationActionStateSubscriberWrapper<T> onFail(Func1<Throwable, MessageActionHolder<T>> factoryFailFunc) {
      this.onFailFactory = factoryFailFunc;
      return this;
   }

   @SuppressWarnings("unused")
   public OperationActionStateSubscriberWrapper<T> onProgress(Action2<T, Integer> onProgress) {
      this.onProgress = onProgress;
      return this;
   }

   public ActionStateSubscriber<T> wrap() {
      return new ActionStateSubscriber<T>().onStart(t -> view.showProgress(progressText))
            .onSuccess(t -> {
               view.hideProgress();
               if (onSuccessAction != null) onSuccessAction.call(t);
            })
            .onFail((t, throwable) -> {
               view.hideProgress();
               final MessageActionHolder<T> failHolder = onFailFactory != null ? onFailFactory.call(throwable) : null;
               if (failHolder != null) {
                  String message = hasActionMessage(failHolder) ? failHolder.message.provide(t) : view.context()
                        .getString(R.string.error_something_went_wrong);
                  Action1<T> action = failHolder.action;
                  view.showError(message, action == null ? null : o -> action.call(t));
               }
            })
            .onProgress((t, integer) -> {
               if (onProgress != null) {
                  onProgress.call(t, integer);
               }
            });
   }

   private boolean hasActionMessage(MessageActionHolder<T> holder) {
      return holder != null && holder.message != null;
   }
}
