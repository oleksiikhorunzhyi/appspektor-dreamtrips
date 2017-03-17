package com.worldventures.dreamtrips.modules.common.presenter;

import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;

import java.io.IOException;

import io.techery.janet.CancelException;
import io.techery.janet.helper.JanetActionException;
import io.techery.janet.http.exception.HttpServiceException;
import timber.log.Timber;

import static com.worldventures.dreamtrips.util.ThrowableUtils.getCauseByType;

/**
 * @deprecated use CommandWithError and Presenter.handleError(action, throwable)
 */
@Deprecated
public class ApiErrorPresenter {

   ApiErrorView apiErrorView;

   public void setView(ApiErrorView apiErrorView) {
      this.apiErrorView = apiErrorView;
   }

   public void dropView() {
      apiErrorView = null;
   }

   public boolean hasView() {
      return apiErrorView != null;
   }

   public void handleError(Throwable exception) {
      if (exception instanceof CancelException) return;
      Timber.e(exception, this.getClass().getName() + " handled caught exception");
      if (!hasView()) {
         Crashlytics.logException(exception);
         Timber.e(exception, "ApiErrorPresenter expects apiErrorView to be set, which is null.");
         return;
      }
      //
      apiErrorView.onApiCallFailed();
      apiErrorView.informUser(R.string.smth_went_wrong);
   }

   public void handleActionError(Object action, Throwable exception) {
      if (exception instanceof CancelException) return;
      Timber.e(exception, this.getClass().getName() + " handled caught exception");
      if (!hasView()) {
         Crashlytics.logException(exception);
         Timber.e(exception, "ApiErrorPresenter expects apiErrorView to be set, which is null.");
         return;
      }
      //
      if (!handleJanetHttpError(action, exception)) {
         apiErrorView.informUser(R.string.smth_went_wrong);
      }
   }

   private boolean handleJanetHttpError(Object action, Throwable exception) {
      if ((action instanceof BaseHttpAction || action instanceof com.worldventures.dreamtrips.api.api_common.BaseHttpAction) && exception instanceof HttpServiceException) {
         apiErrorView.onApiCallFailed();

         ErrorResponse errorResponse = new ErrorResponse();
         com.worldventures.dreamtrips.api.api_common.error.ErrorResponse errorResponseNew = ((com.worldventures.dreamtrips.api.api_common.BaseHttpAction) action)
               .errorResponse();
         if (errorResponseNew != null) errorResponse.setErrors(errorResponseNew.errors());

         if (getCauseByType(IOException.class, exception.getCause()) != null) {
            apiErrorView.informUser(R.string.no_connection);
         } else if (errorResponse != null && errorResponse.getErrors() != null && !errorResponse.getErrors()
               .isEmpty()) {
            logError(errorResponse);
            if (!apiErrorView.onApiError(errorResponse)) apiErrorView.informUser(errorResponse.getFirstMessage());
         } else {
            apiErrorView.informUser(exception.getCause().getLocalizedMessage());
         }
         return true;
      }
      if (exception instanceof JanetActionException) {
         JanetActionException actionError = (JanetActionException) exception;
         return handleJanetHttpError(actionError.getAction(), actionError.getCause());
      }
      if (exception.getCause() != null) {
         return handleJanetHttpError(action, exception.getCause());
      }
      return false;
   }

   private void logError(ErrorResponse errorResponse) {
      StringBuilder stringBuilder = new StringBuilder("Fields failed: ");
      //
      Queryable.from(errorResponse.getErrors()).forEachR(entry -> {
         stringBuilder.append("\n").append(entry.field).append(" : ").append(TextUtils.join(",", entry.errors));
      });
      //
      Timber.e(stringBuilder.toString());
   }
}
