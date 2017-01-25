package com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import java.lang.ref.WeakReference;

import rx.functions.Action1;

public class DialogOperationScreen implements OperationScreen<Dialog> {
   private WeakReference<View> viewRef;

   private Dialog errorDialog;
   private Dialog progressDialog;
   private CancelStrategy cancelStrategy;

   public DialogOperationScreen(@NonNull View view) {
      this.viewRef = new WeakReference<>(view);
      this.cancelStrategy = new SimpleCancelStrategy();

      view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
         @Override
         public void onViewAttachedToWindow(View v) {
         }

         @Override
         public void onViewDetachedFromWindow(View v) {
            hideDialogs();
            v.removeOnAttachStateChangeListener(this);
         }
      });
   }

   @Override
   public void showProgress(@Nullable String text) {
      hideDialogs();

      progressDialog = new MaterialDialog.Builder(context())
            .content(text == null? viewRef.get().getContext().getString(R.string.loading) : text)
            .progress(true, 0)
            .cancelable(cancelStrategy.isCancellable())
            .cancelListener(cancelStrategy.getCancelListener())
            .build();

      progressDialog.show();
   }

   @Override
   public void hideProgress() {
      if (progressDialog != null && progressDialog.isShowing()) {
         progressDialog.dismiss();
      }
   }

   @Override
   public void showError(String msg, Action1<Dialog> action) {
      hideDialogs();

      errorDialog = new MaterialDialog.Builder(context())
            .content(msg)
            .cancelable(false)
            .positiveText(R.string.ok)
            .onNegative((dialog1, which) -> {
               if (action != null) action.call(errorDialog);
            }).onPositive((dialog2, which1) -> {
               if (action != null) action.call(errorDialog);
            })
            .build();
      errorDialog.show();
   }

   @Override
   public Context context() {
      return checkAndGetView().getContext();
   }

   private void hideDialogs() {
      if (progressDialog != null) progressDialog.dismiss();
      if (errorDialog != null) errorDialog.dismiss();
   }

   private View checkAndGetView() {
      View v = viewRef.get();
      if (v == null) {
         throw new IllegalStateException("View == null");
      }

      return v;
   }

   public void setCancelStrategy(CancelStrategy cancelStrategy) {
      this.cancelStrategy = cancelStrategy;
   }
}
