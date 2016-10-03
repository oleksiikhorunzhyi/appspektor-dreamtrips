package com.worldventures.dreamtrips.wallet.ui.common.helper;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.ui.common.base.screen.ErrorScreen;

import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

public class ErrorSubscriberWrapper<T> {

   private final ErrorScreen<T> view;
   @Nullable private Action1<T> onNextAction;
   @Nullable private Func1<Throwable, MessageActionHolder<Throwable>> onFailFactory;

   private ErrorSubscriberWrapper(ErrorScreen<T> view) {
      this.view = view;
   }

   public static <T> ErrorSubscriberWrapper<T> forView(ErrorScreen<T> view) {
      return new ErrorSubscriberWrapper<>(view);
   }

   public ErrorSubscriberWrapper<T> onNext(Action1<T> onNextAction) {
      this.onNextAction = onNextAction;
      return this;
   }

   public ErrorSubscriberWrapper<T> onFail(String message, @Nullable Action1<Throwable> onFail) {
      this.onFailFactory = throwable -> new MessageActionHolder<>(message, onFail);
      return this;
   }

   public ErrorSubscriberWrapper<T> onFail(String message) {
      return onFail(message, null);
   }

   public ErrorSubscriberWrapper<T> onFail(Func1<Throwable, MessageActionHolder<Throwable>> factoryFailFunc) {
      this.onFailFactory = factoryFailFunc;
      return this;
   }

   public Subscriber<T> wrap() {
      return new Subscriber<T>() {

         @Override
         public void onCompleted() {
         }

         @Override
         public void onError(Throwable throwable) {
            final MessageActionHolder<Throwable> failHolder = onFailFactory != null ? onFailFactory.call(throwable) : null;
            String message = failHolder != null ? failHolder.message.provide(throwable) : null;
            view.showError(message, failHolder.action == null ? null : o -> failHolder.action.call(throwable));
         }

         @Override
         public void onNext(T t) {
            if (onNextAction != null) onNextAction.call(t);
         }
      };
   }

}
