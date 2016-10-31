package com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
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

   public DialogOperationScreen(@NonNull View view) {
      this.viewRef = new WeakReference<>(view);

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
   public void showProgress() {
      hideDialogs();

      progressDialog = new MaterialDialog.Builder(context())
            .content(R.string.loading)
            .progress(true, 0)
            .cancelable(false)
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
}
