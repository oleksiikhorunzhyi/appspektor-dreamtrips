package com.worldventures.dreamtrips.core.janet;

import android.content.Context;

import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;

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
         String errorMessage = httpErrorHandlingUtil.handleJanetHttpError(commandAction, e, fallbackMessage);
         commandAction.setErrorMessage(errorMessage);
      }

      return false;
   }
}
