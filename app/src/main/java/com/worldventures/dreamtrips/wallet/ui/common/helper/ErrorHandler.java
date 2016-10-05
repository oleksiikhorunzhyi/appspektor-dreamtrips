package com.worldventures.dreamtrips.wallet.ui.common.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.R;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.techery.janet.command.exception.CommandServiceException;
import io.techery.janet.helper.JanetActionException;
import io.techery.janet.http.exception.HttpServiceException;
import io.techery.janet.smartcard.exception.NotConnectedException;
import io.techery.janet.smartcard.exception.SmartCardServiceException;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class ErrorHandler<T> implements Func1<Throwable, MessageActionHolder<T>> {

   private final Map<Class<? extends Throwable>, String> throwableMessageMap;
   private final Map<Class<? extends Throwable>, Action1<T>> throwableActionMap;
   private final List<Class<? extends Throwable>> ignoredThrowables;
   private final Action1<T> defaultErrorAction;
   private final String defaultErrorMessage;
   private final Context context;

   private ErrorHandler(Builder<T> builder) {
      throwableMessageMap = builder.throwableMessageMap;
      throwableActionMap = builder.throwableActionMap;
      ignoredThrowables = builder.ignoredThrowables;
      defaultErrorAction = builder.defaultErrorAction;
      context = builder.context;
      defaultErrorMessage = builder.defaultErrorMessage;
   }

   @Override
   public MessageActionHolder<T> call(Throwable throwable) {
      Timber.e(throwable, "");
      return createActionHolder(throwable);
   }

   private MessageActionHolder<T> createActionHolder(Throwable throwable) {
      // call recursive method
      return tryCreateActionHolder(throwable, null, null);
   }

   private MessageActionHolder<T> tryCreateActionHolder(Throwable throwable, String message, Action1<T> action) {
      if (ignoredThrowables.contains(throwable.getClass())){
         return null;
      }

      if (message != null && action != null) {
         // terminal branch
         return new MessageActionHolder<>(message, action);
      }
      if (message == null) message = throwableMessageMap.get(throwable.getClass());
      if (action == null) action = throwableActionMap.get(throwable.getClass());

      if (throwable instanceof CommandServiceException) { // CommandServiceException is wrapper
         return tryCreateActionHolder(throwable.getCause(), message, action);
      }
      if (throwable instanceof JanetActionException) { // JanetActionException is wrapper
         return tryCreateActionHolder(throwable.getCause(), message, action);
      }
      if (throwable instanceof SmartCardServiceException) { // SmartCardServiceException is wrapper
         return tryCreateActionHolder(throwable.getCause(), message, action);
      }
      if (throwable instanceof HttpServiceException) { // HttpServiceException is wrapper
         return tryCreateActionHolder(throwable.getCause(), message, action);
      }
      if (throwable instanceof UnknownHostException) {
         if (message == null) message = context.getString(R.string.wallet_no_internet_connection);
      }
      if (throwable instanceof NotConnectedException) {
         if (message == null) message = context.getString(R.string.wallet_smart_card_is_disconnected);
      }
      // common values
      if (message == null) message = defaultErrorMessage;
      if (action == null) action = defaultErrorAction;
      return tryCreateActionHolder(throwable, message, action);
   }

   public static <T> ErrorHandler.Builder<T> builder(Context context) {
      return new Builder<>(context);
   }

   public static <T> ErrorHandler<T> create(Context context) {
      return new Builder<T>(context).build();
   }

   public static <T> ErrorHandler<T> create(Context context, Action1<T> action) {
      return new Builder<T>(context).defaultAction(action).build();
   }

   public static class Builder<T> {

      private final Map<Class<? extends Throwable>, String> throwableMessageMap = new HashMap<>();
      private final Map<Class<? extends Throwable>, Action1<T>> throwableActionMap = new HashMap<>();
      private final List<Class<? extends Throwable>> ignoredThrowables = new ArrayList<>();
      private Action1<T> defaultErrorAction = t -> {}; // stub defaultErrorAction
      private String defaultErrorMessage;
      private final Context context;

      private Builder(Context context) {
         this.context = context;
         this.defaultErrorMessage = context.getString(R.string.error_something_went_wrong);
      }

      public Builder<T> handle(@NonNull Class<? extends Throwable> clazz, @NonNull String message) {
         throwableMessageMap.put(clazz, message);
         return this;
      }

      public Builder<T> handle(Class<? extends Throwable> clazz, @StringRes int stringId) {
         return handle(clazz, context.getString(stringId));
      }

      public Builder<T> handle(@NonNull Class<? extends Throwable> clazz, @NonNull Action1<T> action) {
         throwableActionMap.put(clazz, action);
         return this;
      }

      public Builder<T> ignore(@NonNull Class<? extends Throwable> clazz) {
         ignoredThrowables.add(clazz);
         return this;
      }

      public Builder<T> defaultAction(@NonNull Action1<T> defaultAction) {
         this.defaultErrorAction = defaultAction;
         return this;
      }

      public Builder<T> defaultMessage(@NonNull String message) {
         this.defaultErrorMessage = message;
         return this;
      }

      public Builder<T> defaultMessage(@StringRes int stringId) {
         return defaultMessage(context.getString(stringId));
      }

      public ErrorHandler<T> build() {
         return new ErrorHandler<>(this);
      }
   }
}
