package com.worldventures.dreamtrips.wallet.ui.settings.general.reset;


import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.DialogErrorView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.DialogProgressView;

import io.techery.janet.CancelException;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.ErrorView;
import io.techery.janet.operationsubscriber.view.ProgressView;
import rx.functions.Action0;

public class FactoryResetOperationView extends ComposableOperationView<ResetSmartCardCommand> {

   private FactoryResetOperationView(ProgressView<ResetSmartCardCommand> progressView, ErrorView<ResetSmartCardCommand> errorView) {
      super(progressView, errorView);
   }

   public static FactoryResetOperationView create(Context context, Action0 retryAction, Action0 cancelAction,
         int errorTitleResId, int errorContentResId, int positiveResId, int negativeResId, int progressContentResId, boolean cancelable) {
      final FactoryResetErrorView factoryResetErrorView = new FactoryResetErrorView(context, retryAction,
            cancelAction, errorTitleResId, errorContentResId, positiveResId, negativeResId);

      final FactoryResetProgressView factoryResetProgressView = new FactoryResetProgressView(context, cancelAction,
            progressContentResId, cancelable);

      return new FactoryResetOperationView(factoryResetProgressView, factoryResetErrorView);
   }

   private static class FactoryResetErrorView extends DialogErrorView {
      private final Action0 retryAction;
      private final Action0 cancelAction;
      private final int titleResId;
      private final int contentResId;
      private final int positiveResId;
      private final int negativeResId;

      protected FactoryResetErrorView(Context context, Action0 retryAction, Action0 cancelAction, int titleResId,
            int contentResId, int positiveResId, int negativeResId) {
         super(context);
         this.retryAction = retryAction;
         this.cancelAction = cancelAction;
         this.titleResId = titleResId;
         this.contentResId = contentResId;
         this.positiveResId = positiveResId;
         this.negativeResId = negativeResId;
      }

      @Override
      protected MaterialDialog createDialog(Object o, Throwable throwable, Context context) {
         return new MaterialDialog.Builder(context)
               .title(titleResId)
               .content(contentResId)
               .positiveText(positiveResId)
               .negativeText(negativeResId)
               .onPositive((dialog, which) -> retryAction.call())
               .onNegative((dialog, which) -> cancelAction.call())
               .build();
      }

      @Override
      public void showError(Object o, Throwable throwable) {
         if(!(throwable instanceof CancelException)) {
            super.showError(o, throwable);
         }
      }
   }

   private static class FactoryResetProgressView extends DialogProgressView {
      private final Action0 cancelAction;
      private final int contentResId;
      private final boolean cancelable;

      public FactoryResetProgressView(Context context, Action0 cancelAction, int contentResId, boolean cancelable) {
         super(context);
         this.cancelAction = cancelAction;
         this.contentResId = contentResId;
         this.cancelable = cancelable;
      }

      @Override
      protected MaterialDialog createDialog(Object o, Context context) {
         return new MaterialDialog.Builder(context)
               .progress(true, 0)
               .content(contentResId)
               .cancelable(cancelable)
               .canceledOnTouchOutside(cancelable)
               .cancelListener(dialogInterface -> cancelAction.call())
               .build();
      }
   }
}
