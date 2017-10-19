package com.worldventures.dreamtrips.core.janet;

import android.content.Context;

import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.core.janet.CommandWithError;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.CommandActionService;
import io.techery.janet.JanetException;

public class DreamTripsCommandService extends ActionServiceWrapper {

   private Context appContext;
   private HttpErrorHandlingUtil httpErrorHandlingUtil;


   public DreamTripsCommandService(Context appContext, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      super(new CommandActionService());
      this.appContext = appContext;
      this.httpErrorHandlingUtil = httpErrorHandlingUtil;
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
         String noConnectionMessage = appContext.getString(R.string.no_connection);
         String errorMessage = httpErrorHandlingUtil.handleJanetHttpError(commandAction, e, fallbackMessage, noConnectionMessage);
         commandAction.setErrorMessage(errorMessage);
      }

      return false;
   }
}
