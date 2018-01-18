package com.worldventures.wallet.ui.common.helper2.error.http;

import android.content.Context;
import android.support.annotation.StringRes;

import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.wallet.R;
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.error.SimpleErrorView;

import org.jetbrains.annotations.Nullable;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.techery.janet.helper.JanetActionException;
import io.techery.janet.http.exception.HttpException;
import io.techery.janet.http.exception.HttpServiceException;
import io.techery.janet.http.model.Response;
import io.techery.janet.operationsubscriber.view.ErrorView;
import rx.functions.Action1;

public class HttpErrorViewProvider<T> implements ErrorViewProvider<T> {

   private final Context context;
   private final HttpErrorHandlingUtil httpErrorHandlingUtil;
   private final Action1<T> retryAction;
   private final Action1<T> cancelAction;
   @StringRes private final int titleRes;
   @StringRes private final int contentRes;

   public HttpErrorViewProvider(Context context, HttpErrorHandlingUtil httpErrorHandlingUtil, Action1<T> retryAction, Action1<T> cancelAction) {
      this(context, httpErrorHandlingUtil, retryAction, cancelAction, 0, 0);
   }

   public HttpErrorViewProvider(Context context, HttpErrorHandlingUtil httpErrorHandlingUtil, Action1<T> retryAction,
         Action1<T> cancelAction, @StringRes int titleRes) {
      this(context, httpErrorHandlingUtil, retryAction, cancelAction, titleRes, 0);
   }

   public HttpErrorViewProvider(Context context, HttpErrorHandlingUtil httpErrorHandlingUtil, Action1<T> retryAction,
         Action1<T> cancelAction, @StringRes int titleRes, @StringRes int contentRes) {
      this.context = context;
      this.httpErrorHandlingUtil = httpErrorHandlingUtil;
      this.retryAction = retryAction;
      this.cancelAction = cancelAction;
      this.titleRes = titleRes;
      this.contentRes = contentRes;
   }

   @Override
   public Class<? extends Throwable> forThrowable() {
      return HttpServiceException.class;
   }

   @Override
   @Nullable
   public ErrorView<T> create(T t, @Nullable Throwable parentThrowable, Throwable throwable) {
      throwable = throwable.getCause();

      boolean parentIsJanetException = parentThrowable instanceof JanetActionException;

      if (throwable instanceof HttpException) {
         final Response response = ((HttpException) throwable).getResponse();
         if (response != null && response.getStatus() >= 500) {
            return new SimpleErrorView<>(context, titleRes,
                  validateForceContentMessage(context.getString(R.string.wallet_error_internal_server)), cancelAction);
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
               context.getString(R.string.wallet_no_internet_connection));
         if (httpErrorMessage == null) {
            return null;
         }
         return new SimpleErrorView<>(context, titleRes, validateForceContentMessage(httpErrorMessage),
               retryAction, R.string.action_retry, cancelAction, R.string.action_cancel);
      }

      if (throwable instanceof UnknownHostException || throwable instanceof ConnectException) {
         return new ConnectionErrorView<>(context,
               validateForceContentMessage(context.getString(R.string.wallet_no_internet_connection)), retryAction, cancelAction);
      }

      if (throwable instanceof SocketTimeoutException) {
         return new ConnectionErrorView<>(context,
               validateForceContentMessage(context.getString(R.string.wallet_connection_timed_out)), retryAction, cancelAction);
      }
      return null;
   }

   private String validateForceContentMessage(String predefinedMessage) {
      return (contentRes != 0) ? context.getString(contentRes) : predefinedMessage;
   }
}
