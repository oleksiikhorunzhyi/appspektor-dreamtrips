package com.worldventures.dreamtrips.core.janet;

import android.content.Context;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.BaseHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;

import java.io.IOException;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.CommandActionService;
import io.techery.janet.JanetException;
import io.techery.janet.helper.JanetActionException;
import io.techery.janet.http.exception.HttpServiceException;

import static com.worldventures.dreamtrips.util.ThrowableUtils.getCauseByType;

public class DreamTripsCommandService extends ActionServiceWrapper {

   private Context appContext;

   public DreamTripsCommandService(Context appContext) {
      super(new CommandActionService());
      this.appContext = appContext;
   }

   @Override
   protected <A> boolean onInterceptSend(ActionHolder<A> holder) throws JanetException {
      return false;
   }

   @Override
   protected <A> void onInterceptCancel(ActionHolder<A> holder) {

   }

   @Override
   protected <A> void onInterceptStart(ActionHolder<A> holder) {

   }

   @Override
   protected <A> void onInterceptProgress(ActionHolder<A> holder, int progress) {

   }

   @Override
   protected <A> void onInterceptSuccess(ActionHolder<A> holder) {

   }

   @Override
   protected <A> boolean onInterceptFail(ActionHolder<A> holder, JanetException e) {
      if (holder.action() instanceof CommandWithError) {
         CommandWithError commandAction = (CommandWithError) holder.action();
         String fallbackMessage = appContext.getString(commandAction.getFallbackErrorMessage());
         String errorMessage = handleJanetHttpError(commandAction, e, fallbackMessage);
         commandAction.setErrorMessage(errorMessage);
      }

      return false;
   }

   private String handleJanetHttpError(Object action, Throwable exception, String fallbackMessage) {
      if ((action instanceof BaseHttpAction || action instanceof com.worldventures.dreamtrips.api.api_common.BaseHttpAction) && exception instanceof HttpServiceException) {

         ErrorResponse errorResponse;
         if (action instanceof BaseHttpAction) {
            errorResponse = ((BaseHttpAction) action).getErrorResponse();
         } else {
            errorResponse = new ErrorResponse();
            com.worldventures.dreamtrips.api.api_common.error.ErrorResponse errorResponseNew = ((com.worldventures.dreamtrips.api.api_common.BaseHttpAction) action)
                  .errorResponse();
            if (errorResponseNew != null) errorResponse.setErrors(errorResponseNew.errors());
         }

         if (getCauseByType(IOException.class, exception.getCause()) != null) {
            return appContext.getString(R.string.no_connection);
         } else if (errorResponse != null && errorResponse.getErrors() != null && !errorResponse.getErrors()
               .isEmpty()) {
            return errorResponse.getFirstMessage();
         } else return fallbackMessage;
      }

      if (exception instanceof JanetActionException) {
         JanetActionException actionError = (JanetActionException) exception;
         return handleJanetHttpError(actionError.getAction(), actionError.getCause(), fallbackMessage);
      }

      if (exception.getCause() != null) {
         return handleJanetHttpError(action, exception.getCause(), fallbackMessage);
      }

      return fallbackMessage;
   }

}
