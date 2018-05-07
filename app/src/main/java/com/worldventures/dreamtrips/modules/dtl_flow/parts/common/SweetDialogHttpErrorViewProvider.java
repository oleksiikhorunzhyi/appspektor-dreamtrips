package com.worldventures.dreamtrips.modules.dtl_flow.parts.common;

import android.content.Context;

import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.util.Action;
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewProvider;

import org.jetbrains.annotations.Nullable;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.techery.janet.helper.JanetActionException;
import io.techery.janet.http.exception.HttpException;
import io.techery.janet.http.exception.HttpServiceException;
import io.techery.janet.http.model.Response;
import io.techery.janet.operationsubscriber.view.ErrorView;

public class SweetDialogHttpErrorViewProvider<T> implements ErrorViewProvider<T> {

   private final Context context;
   private final HttpErrorHandlingUtil httpErrorHandlingUtil;
   private final Action<T> retryAction;

   public SweetDialogHttpErrorViewProvider(Context context, HttpErrorHandlingUtil httpErrorHandlingUtil, Action<T> retryAction) {
      this.context = context;
      this.httpErrorHandlingUtil = httpErrorHandlingUtil;
      this.retryAction = retryAction;
   }

   @Override
   public Class<? extends Throwable> forThrowable() {
      return HttpServiceException.class;
   }

   @Override
   @Nullable
   public ErrorView<T> create(T t, @Nullable Throwable parentThrowable, Throwable throwable) {
      throwable = throwable instanceof HttpServiceException ? throwable.getCause() : throwable;

      boolean parentIsJanetException = parentThrowable instanceof JanetActionException;

      if (throwable instanceof HttpException) {
         final Response response = ((HttpException) throwable).getResponse();
         if (response != null && response.getStatus() >= 500) {
            return createConnectionDialog(context.getString(com.worldventures.wallet.R.string.wallet_error_internal_server));
         } else if (throwable.getCause() != null) {
            // if there is no response at all - it might be connection exception, try to handle cause
            throwable = throwable.getCause();
         }
      }

      if (throwable instanceof JanetActionException || parentIsJanetException) {
         if (parentIsJanetException) {
            throwable = parentThrowable;
         }

         final Object action = ((JanetActionException) throwable).getAction();
         final String httpErrorMessage = httpErrorHandlingUtil.handleJanetHttpError(action, throwable, null,
               context.getString(R.string.no_connection));
         if (httpErrorMessage == null) {
            return null;
         }
         return createConnectionDialog(httpErrorMessage);
      }

      if (throwable instanceof UnknownHostException || throwable instanceof ConnectException || throwable instanceof SocketTimeoutException) {
         return createConnectionDialog(context.getString(R.string.no_connection));
      }
      return null;
   }

   private DialogErrorView<T> createConnectionDialog(String message) {
      return new SimpleSweetDialogErrorView<>(context, SweetDialogParams.forNetworkUnAvailableErrorView(context, message, retryAction));
   }
}
