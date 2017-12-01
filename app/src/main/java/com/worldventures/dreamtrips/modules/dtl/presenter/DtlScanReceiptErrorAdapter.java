package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.content.Context;

import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.view.util.DtlApiErrorViewAdapter;

import org.apache.http.HttpStatus;

import io.techery.janet.helper.JanetActionException;
import io.techery.janet.http.exception.HttpException;
import io.techery.janet.http.exception.HttpServiceException;

public class DtlScanReceiptErrorAdapter extends DtlApiErrorViewAdapter {
   private DtlScanReceiptPresenter.View dialogView;

   public DtlScanReceiptErrorAdapter(Context context, HttpErrorHandlingUtil errorHandlingUtils) {
      super(context, errorHandlingUtils);
   }

   public void showError(Throwable exception) {
      if (isUnprocessableEntityError(exception)) {
         JanetActionException janetActionException = (JanetActionException) exception;
         dialogView.showErrorDialog(errorHandlingUtils.handleJanetHttpError(janetActionException.getAction(), exception,
               context.getString(R.string.smth_went_wrong), context.getString(R.string.no_connection)));
      } else {
         handleError(exception);
      }
   }

   public boolean isUnprocessableEntityError(Throwable exception) {
      if (exception instanceof JanetActionException) {
         return isUnprocessableEntityError(exception.getCause());
      }
      if (exception instanceof HttpServiceException) {
         return isUnprocessableEntityError(exception.getCause());
      }
      if (exception instanceof HttpException) {
         HttpException httpException = (HttpException) exception;
         return httpException.getResponse().getStatus() == HttpStatus.SC_UNPROCESSABLE_ENTITY;
      }
      return false;
   }

   public void setDialogView(DtlScanReceiptPresenter.View dialogView) {
      this.dialogView = dialogView;
   }
}
