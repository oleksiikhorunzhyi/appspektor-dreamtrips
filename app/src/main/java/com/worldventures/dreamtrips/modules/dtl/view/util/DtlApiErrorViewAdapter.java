package com.worldventures.dreamtrips.modules.dtl.view.util;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.InformView;
import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;

import io.techery.janet.CancelException;
import io.techery.janet.helper.JanetActionException;
import timber.log.Timber;

/**
 * @deprecated use CommandWithError and Presenter.handleError(action, throwable)
 */
@Deprecated
public class DtlApiErrorViewAdapter {

   private final Context context;
   private final HttpErrorHandlingUtil errorHandlingUtils;
   private ApiErrorView view;

   public DtlApiErrorViewAdapter(Context context, HttpErrorHandlingUtil errorHandlingUtils) {
      this.context = context;
      this.errorHandlingUtils = errorHandlingUtils;
   }

   public void setView(ApiErrorView informView) {
      this.view = informView;
   }

   public void dropView() {
      view = null;
   }

   public boolean hasView() {
      return view != null;
   }

   public void handleError(Throwable exception) {
      if (unhandled(exception)) return;
      if (exception instanceof JanetActionException) {
         JanetActionException janetActionException = (JanetActionException) exception;
         handleError(janetActionException.getAction(), janetActionException.getCause());
         return;
      }
      //
      view.informUser(R.string.smth_went_wrong);
      view.onApiError();
   }

   public void handleError(Object action, Throwable exception) {
      if (unhandled(exception)) return;
      //
      view.informUser(errorHandlingUtils.handleJanetHttpError(action, exception, context.getString(R.string.smth_went_wrong)));
      view.onApiError();
   }

   private boolean unhandled(Throwable exception) {
      if (exception instanceof CancelException) return true;
      Timber.e(exception, this.getClass().getName() + " handled caught exception");
      if (hasView()) return false;
      //
      Crashlytics.logException(exception);
      Timber.e(exception, "DtlApiErrorViewAdapter expects view to be set, which is null.");
      return true;
   }

   public interface ApiErrorView extends InformView {
      void onApiError();
   }

}
