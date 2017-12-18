package com.worldventures.dreamtrips.core.janet;

import android.content.Context;
import android.util.Pair;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;

import org.jetbrains.annotations.Nullable;

import io.techery.janet.ActionHolder;
import io.techery.janet.JanetException;

import static com.worldventures.core.utils.HttpErrorHandlingUtil.isNoInternetConnectionError;
import static com.worldventures.core.utils.HttpErrorHandlingUtil.obtainHttpErrorMessage;
import static com.worldventures.core.utils.HttpErrorHandlingUtil.obtainHttpErrorPath;
import static com.worldventures.core.utils.HttpErrorHandlingUtil.obtainHttpException;

public class DreamTripsCommandServiceWrapper extends DreamTripsCommandService {

   private HttpFailListener failListener;

   public DreamTripsCommandServiceWrapper(Context appContext, HttpErrorHandlingUtil util) {
      super(appContext, util);
   }

   public void setFailListener(HttpFailListener failListener) {
      this.failListener = failListener;
   }

   @Override
   protected <A> boolean onInterceptFail(ActionHolder<A> holder, JanetException e) {
      boolean parentResult = super.onInterceptFail(holder, e);

      Pair<BaseHttpAction, Throwable> errorPair = obtainHttpException(holder, e);
      //we intercept http errors and not from login (only login is nor authorized)
      if (errorPair == null || !(errorPair.first instanceof AuthorizedHttpAction)) {
         return parentResult;
      }

      Throwable cause = errorPair.second;

      if (isNoInternetConnectionError(cause)) {
         notifyListener(true, null, null);
      } else {
         String errorPath = obtainHttpErrorPath(cause);
         String errorText = obtainErrorMessage(holder, cause);

         if (errorText != null && !errorText.isEmpty() && errorPath != null && !errorPath.isEmpty()) {
            notifyListener(false, errorPath, errorText);
         }
      }

      return parentResult;
   }

   private static String obtainErrorMessage(ActionHolder holder, Throwable cause) {
      if (holder.action() instanceof CommandWithError) {
         //humanized error message is already set in parent class
         return ((CommandWithError) holder.action()).getErrorMessage();
      } else {
         return obtainHttpErrorMessage(cause);
      }
   }

   private void notifyListener(boolean noInternet, String path, String errorMessage) {
      if (failListener != null) {
         failListener.onFail(noInternet, path, errorMessage);
      }
   }

   public interface HttpFailListener {
      void onFail(boolean noInternet, @Nullable String path, @Nullable String errorMessage);
   }

}
