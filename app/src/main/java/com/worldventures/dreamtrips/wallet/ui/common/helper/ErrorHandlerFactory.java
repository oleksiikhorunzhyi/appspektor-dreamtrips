package com.worldventures.dreamtrips.wallet.ui.common.helper;


import android.content.Context;

import com.worldventures.dreamtrips.core.utils.HttpErrorHandlingUtil;

import rx.functions.Action1;

public class ErrorHandlerFactory {

   private final Context context;
   private final HttpErrorHandlingUtil errorHandlingUtils;

   public ErrorHandlerFactory(Context context, HttpErrorHandlingUtil errorHandlingUtils) {
      this.context = context;
      this.errorHandlingUtils = errorHandlingUtils;
   }

   public <T> ErrorHandler.Builder<T> builder() {
      return new ErrorHandler.Builder<>(context, errorHandlingUtils);
   }

   public <T> ErrorHandler<T> errorHandler() {
      return new ErrorHandler.Builder<T>(context, errorHandlingUtils).build();
   }

   public <T> ErrorHandler<T> errorHandler(Action1<T> action) {
      return new ErrorHandler.Builder<T>(context, errorHandlingUtils).defaultAction(action).build();
   }
}
