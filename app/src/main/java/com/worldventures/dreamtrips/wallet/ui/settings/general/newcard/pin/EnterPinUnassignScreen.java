package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.pin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class EnterPinUnassignScreen extends WalletLinearLayout<EnterPinUnassignPresenter.Screen, EnterPinUnassignPresenter, EnterPinUnassignPath> implements EnterPinUnassignPresenter.Screen {

   private MaterialDialog errorEnterPinDialog = null;

   @InjectView(R.id.toolbar) Toolbar toolbar;

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
   protected void onFinishInflate() {
      super.onFinishInflate();
      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> presenter.goBack());
   }

   @Override
   public OperationView<ResetSmartCardCommand> provideOperationView() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<ResetSmartCardCommand>(
                  getContext(), R.string.loading, false, dialog -> presenter.goBack()
            )
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
               .onNegative((dialog, which) -> presenter.goBack())
               .build();
      }
      if (!errorEnterPinDialog.isShowing()) errorEnterPinDialog.show();
   }

   @Override
   protected void onDetachedFromWindow() {
      if (errorEnterPinDialog != null) errorEnterPinDialog.dismiss();
      super.onDetachedFromWindow();
   }
}