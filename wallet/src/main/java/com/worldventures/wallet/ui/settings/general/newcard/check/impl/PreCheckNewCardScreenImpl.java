package com.worldventures.wallet.ui.settings.general.newcard.check.impl;

import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.core.utils.ProjectTextUtils;
import com.worldventures.wallet.R;
import com.worldventures.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.settings.general.newcard.check.PreCheckNewCardPresenter;
import com.worldventures.wallet.ui.settings.general.newcard.check.PreCheckNewCardScreen;
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetDelegate;
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetOperationView;
import com.worldventures.wallet.ui.widget.WalletCheckWidget;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.OperationView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PreCheckNewCardScreenImpl extends WalletBaseController<PreCheckNewCardScreen, PreCheckNewCardPresenter> implements PreCheckNewCardScreen {

   private Button nextBtn;
   private WalletCheckWidget checkWidgetBluetooth;
   private WalletCheckWidget checkWidgetConnection;

   @Inject PreCheckNewCardPresenter presenter;

   private MaterialDialog addCardContinueDialog = null;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      nextBtn = view.findViewById(R.id.btn_next);
      nextBtn.setOnClickListener(btnNext -> getPresenter().prepareContinueAddCard());
      checkWidgetBluetooth = view.findViewById(R.id.check_widget_bluetooth);
      checkWidgetConnection = view.findViewById(R.id.check_widget_connection);
   }

   @Override
   public void showAddCardContinueDialog(String scId) {
      if (addCardContinueDialog == null) {
         addCardContinueDialog = new MaterialDialog.Builder(getContext())
               .content(ProjectTextUtils.fromHtml(getString(R.string.wallet_new_card_pre_install_confirm_message, scId)))
               .positiveText(R.string.wallet_continue_label)
               .onPositive((dialog, which) -> getPresenter().navigateNext())
               .onNegative((dialog, which) -> getPresenter().goBack())
               .negativeText(R.string.wallet_cancel_label)
               .build();
      }
      if (!addCardContinueDialog.isShowing()) {
         addCardContinueDialog.show();
      }
   }

   @Override
   public void nextButtonEnabled(boolean enable) {
      nextBtn.setEnabled(enable);
   }

   @Override
   public void bluetoothEnable(boolean enabled) {
      checkWidgetBluetooth.setChecked(enabled);
   }

   @Override
   public void cardConnected(boolean connected) {
      checkWidgetConnection.setChecked(connected);
   }

   @Override
   public void setVisiblePowerSmartCardWidget(boolean visible) {
      checkWidgetConnection.setVisibility(visible ? VISIBLE : GONE);
   }

   @Override
   protected void onDetach(@NonNull View view) {
      if (addCardContinueDialog != null) {
         addCardContinueDialog.dismiss();
      }
      super.onDetach(view);
   }

   @Override
   public OperationView<ResetSmartCardCommand> provideResetOperationView(FactoryResetDelegate factoryResetDelegate) {
      return FactoryResetOperationView.create(getContext(),
            factoryResetDelegate::factoryReset,
            () -> {
            },
            R.string.wallet_error_enter_pin_title,
            R.string.wallet_error_enter_pin_msg,
            R.string.wallet_retry_label,
            R.string.wallet_cancel_label,
            R.string.wallet_loading,
            false);
   }

   @Override
   public PreCheckNewCardPresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_pre_check_new_card, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }
}
