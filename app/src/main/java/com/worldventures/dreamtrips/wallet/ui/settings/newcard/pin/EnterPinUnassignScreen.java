package com.worldventures.dreamtrips.wallet.ui.settings.newcard.pin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.FactoryResetCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.settings.newcard.helper.ProgressDialogView;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class EnterPinUnassignScreen extends WalletLinearLayout<EnterPinUnassignPresenter.Screen, EnterPinUnassignPresenter, EnterPinUnassignPath> implements EnterPinUnassignPresenter.Screen {

   private ProgressDialogView progressDialogView = null;
   private MaterialDialog errorEnterPinDialog = null;

   public EnterPinUnassignScreen(Context context) {
      super(context);
   }

   public EnterPinUnassignScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public EnterPinUnassignPresenter createPresenter() {
      return new EnterPinUnassignPresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @Override
   public OperationView<FactoryResetCommand> provideOperationView() {
      return new ComposableOperationView<>(
            progressDialogView = ProgressDialogView.<FactoryResetCommand>builder(getContext())
                  .message(R.string.loading)
                  .build(),
            ErrorViewFactory.<FactoryResetCommand>builder().build()
      );
   }

   @Override
   public void showErrorEnterPinDialog() {
      if (errorEnterPinDialog == null) {
         errorEnterPinDialog = new MaterialDialog.Builder(getContext())
               .title(R.string.wallet_error_enter_pin_title)
               .content(R.string.wallet_error_enter_pin_msg)
               .positiveText(R.string.retry)
               .negativeText(R.string.cancel)
               .onPositive((dialog, which) -> presenter.retryEnterPinAndUnassign())
               .onNegative((dialog, which) -> presenter.cancelUnassign())
               .build();
      }
      if(!errorEnterPinDialog.isShowing()) errorEnterPinDialog.show();
   }

   @Override
   protected void onDetachedFromWindow() {
      if(progressDialogView != null) progressDialogView.hideProgress();
      if(errorEnterPinDialog != null) errorEnterPinDialog.dismiss();
      super.onDetachedFromWindow();
   }
}