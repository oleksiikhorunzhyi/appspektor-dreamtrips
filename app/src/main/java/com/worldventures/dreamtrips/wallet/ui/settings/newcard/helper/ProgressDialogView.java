package com.worldventures.dreamtrips.wallet.ui.settings.newcard.helper;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

import io.techery.janet.operationsubscriber.view.ProgressView;

public class ProgressDialogView<T> implements ProgressView<T> {

   private Context context;
   private MaterialDialog dialog = null;

   private ProgressDialogView(Context context, MaterialDialog.Builder dialogBuilder) {
      this.context = context;
      createDialog(dialogBuilder);
   }

   private void createDialog(MaterialDialog.Builder builder) {
      dialog = builder
            .progress(true, 0)
            .build();
   }

   @Override
   public void showProgress(T t) {
      dialog.show();
   }

   @Override
   public boolean isProgressVisible() {
      return dialog != null && dialog.isShowing();
   }

   @Override
   public void hideProgress() {
      dialog.dismiss();
   }

   public static <T> Builder<T> builder(Context context) {
      return new Builder<>(context);
   }

   public static class Builder<T> {

      private Context context;
      private MaterialDialog.Builder dialogBuilder;

      private Builder(Context context) {
         dialogBuilder = new MaterialDialog.Builder(context);
         this.context = context;
      }

      public Builder<T> message(int stringRes) {
         dialogBuilder.content(stringRes);
         return this;
      }

      public Builder<T> cancelable(boolean cancelable) {
         dialogBuilder.cancelable(cancelable);
         return this;
      }

      public Builder<T> canceledOnTouchOutside(boolean cancelable) {
         dialogBuilder.canceledOnTouchOutside(cancelable);
         return this;
      }

      public ProgressDialogView<T> build() {
         return new ProgressDialogView<>(context, dialogBuilder);
      }
   }
}
