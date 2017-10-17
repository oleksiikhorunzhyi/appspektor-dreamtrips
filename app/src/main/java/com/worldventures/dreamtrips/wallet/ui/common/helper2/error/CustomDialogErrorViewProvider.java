package com.worldventures.dreamtrips.wallet.ui.common.helper2.error;

import com.afollestad.materialdialogs.MaterialDialog;

import org.jetbrains.annotations.Nullable;

import io.techery.janet.operationsubscriber.view.ErrorView;

public class CustomDialogErrorViewProvider<T> implements ErrorViewProvider<T> {

   private final MaterialDialog materialDialog;
   private final Class<? extends Throwable> throwable;

   public CustomDialogErrorViewProvider(MaterialDialog dialog, Class<? extends Throwable> throwable) {
      this.materialDialog = dialog;
      this.throwable = throwable;
   }

   @Override
   public Class<? extends Throwable> forThrowable() {
      return throwable;
   }

   @Nullable
   @Override
   public ErrorView<T> create(T t, @Nullable Throwable parentThrowable, Throwable throwable) {
      return new ErrorView<T>() {
         @Override
         public void showError(T t, Throwable throwable) {
            if (materialDialog != null) {
               materialDialog.show();
            }
         }

         @Override
         public boolean isErrorVisible() {
            return materialDialog != null && materialDialog.isShowing();
         }

         @Override
         public void hideError() {
            if (materialDialog != null) {
               materialDialog.dismiss();
            }
         }
      };
   }
}
