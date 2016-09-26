package com.worldventures.dreamtrips.wallet.ui.common.helper;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;

public final class OperationSubscriberWrapper<T> {
   public static <T> OperationSubscriberWrapper<T> forView(OperationScreen view) {
      return new OperationSubscriberWrapper<>(view);
   }

   private OperationScreen view;

   private Func1<Throwable, MessageActionHolder<T>> onFailFactory;
   private Action2<T, Integer> onProgress;

   private @Nullable Action1<T> onSuccesAction;

   private OperationSubscriberWrapper(OperationScreen view) {
      this.view = view;
   }

   public OperationSubscriberWrapper<T> onSuccess(@Nullable Action1<T> onSuccess) {
      this.onSuccesAction = onSuccess;
      return this;
   }

   public OperationSubscriberWrapper<T> onFail(String message, @Nullable Action1<T> onFail) {
      this.onFailFactory = throwable -> new MessageActionHolder<>(message, onFail);
      return this;
   }

   public OperationSubscriberWrapper<T> onFail(String message) {
      return onFail(message, null);
   }

   public OperationSubscriberWrapper<T> onFail(Func1<Throwable, MessageActionHolder<T>> factoryFailFunc) {
      this.onFailFactory = factoryFailFunc;
      return this;
   }

   @SuppressWarnings("unused")
   public OperationSubscriberWrapper<T> onProgress(Action2<T, Integer> onProgress) {
      this.onProgress = onProgress;
      return this;
   }

   public ActionStateSubscriber<T> wrap() {
      return new ActionStateSubscriber<T>().onStart(t -> view.showProgress())
            .onSuccess(t -> {
               view.hideProgress();
               if (onSuccesAction != null) onSuccesAction.call(t);
            })
            .onFail((t, throwable) -> {
               final MessageActionHolder<T> failHolder = onFailFactory != null ? onFailFactory.call(throwable) : null;
               String message = hasActionMessage(failHolder) ? failHolder.message.provide(t) : view.context()
                     .getString(R.string.error_something_went_wrong);

               view.hideProgress();
               view.showError(message, failHolder.action == null ? null : o -> failHolder.action.call(t));
            })
            .onProgress((t, integer) -> {
               if (onProgress != null) {
                  onProgress.call(t, integer);
               }
            });
   }

   public static final class MessageActionHolder<T> {
      Action1<T> action;
      MessageProvider<T> message;

      public MessageActionHolder(String message, Action1<T> action) {
         this(t -> message, action);
      }

      public MessageActionHolder(MessageProvider<T> message, Action1<T> action) {
         this.message = message;
         this.action = action;
      }
   }

   public interface MessageProvider<T> {
      MessageProvider NULL = action -> null;

      String provide(T action);
   }

   private boolean hasActionMessage(MessageActionHolder<T> holder) {
      return holder != null && holder.message != null;
   }
}
