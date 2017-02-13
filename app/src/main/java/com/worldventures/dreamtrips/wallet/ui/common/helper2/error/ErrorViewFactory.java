package com.worldventures.dreamtrips.wallet.ui.common.helper2.error;

import com.worldventures.dreamtrips.util.HttpUploaderyException;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.techery.janet.JanetException;
import io.techery.janet.operationsubscriber.view.ErrorView;

public class ErrorViewFactory<T> implements ErrorView<T> {

   private final List<ErrorViewProvider<T>> errorViewFactories;
   private final ErrorView<T> defaultErrorView;

   private ErrorViewFactory(Builder<T> builder) {
      errorViewFactories = new ArrayList<>(builder.providers);
      defaultErrorView = builder.defaultErrorView;
   }

   @Nullable
   private ErrorViewAdapter<T> currentErrorView;

   @Override
   public void showError(T t, Throwable throwable) {
      currentErrorView = createErrorView(throwable, t);
      currentErrorView.show();
   }

   @Override
   public boolean isErrorVisible() {
      return currentErrorView != null && currentErrorView.isVisible();
   }

   @Override
   public void hideError() {
      if (currentErrorView != null) {
         currentErrorView.hide();
      }
   }

   public static <T> Builder<T> builder() {
      return new Builder<>();
   }

   private ErrorViewAdapter<T> createErrorView(Throwable throwable, T t) {

      for (ErrorViewProvider<T> provider : errorViewFactories) {
         if (provider.forThrowable().isInstance(throwable)) {
            return new ErrorViewAdapter<>(provider.create(t, throwable), throwable, t);
         }
      }

      if (throwable instanceof JanetException || throwable instanceof HttpUploaderyException) { // CommandServiceException, JanetActionException, SmartCardServiceException, HttpUploaderyException
         return createErrorView(throwable.getCause(), t);
      }
      // default values
      return new ErrorViewAdapter<>(defaultErrorView, throwable, t);
   }

   public static class Builder<T> {

      private final List<ErrorViewProvider<T>> providers = new LinkedList<>();
      private ErrorView<T> defaultErrorView;

      private Builder() {
      }

      public Builder<T> defaultErrorView(@Nullable ErrorView<T> defaultErrorView) {
         this.defaultErrorView = defaultErrorView;
         return this;
      }

      public Builder<T> addProvider(ErrorViewProvider<T> provider) {
         providers.add(provider);
         return this;
      }

      public ErrorViewFactory<T> build() {
         return new ErrorViewFactory<>(this);
      }
   }

   private static class ErrorViewAdapter<T> {

      private final ErrorView<T> errorView;
      private final Throwable throwable;
      private final T t;

      ErrorViewAdapter(@Nullable ErrorView<T> errorView, Throwable throwable, T t) {
         this.errorView = errorView;
         this.throwable = throwable;
         this.t = t;
      }

      public void show() {
         if(errorView != null) errorView.showError(t, throwable);
      }

      public void hide() {
         if(errorView != null) errorView.hideError();
      }

      public boolean isVisible() {
         return errorView != null && errorView.isErrorVisible();
      }
   }

}
