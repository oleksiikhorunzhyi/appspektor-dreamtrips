package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.check.impl;


import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.check.PreCheckNewCardPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.check.PreCheckNewCardScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetOperationView;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletCheckWidget;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.OperationView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PreCheckNewCardScreenImpl extends WalletBaseController<PreCheckNewCardScreen, PreCheckNewCardPresenter> implements PreCheckNewCardScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.btn_next) Button nextBtn;
   @InjectView(R.id.check_widget_bluetooth) WalletCheckWidget checkWidgetBluetooth;
   @InjectView(R.id.check_widget_connection) WalletCheckWidget checkWidgetConnection;

   @Inject PreCheckNewCardPresenter presenter;

   private MaterialDialog addCardContinueDialog = null;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
   }

   @Override
   public void showAddCardContinueDialog(String scId) {
      if (addCardContinueDialog == null) {
         addCardContinueDialog = new MaterialDialog.Builder(getContext())
               .content(Html.fromHtml(getString(R.string.wallet_new_card_pre_install_confirm_message, scId)))
               .positiveText(R.string.wallet_continue_label)
               .onPositive((dialog, which) -> getPresenter().navigateNext())
               .onNegative((dialog, which) -> getPresenter().goBack())
               .negativeText(R.string.cancel)
               .build();
      }
      if (!addCardContinueDialog.isShowing()) addCardContinueDialog.show();
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

   @OnClick(R.id.btn_next)
   public void onClickNext() {
      presenter.prepareContinueAddCard();
   }

   @Override
   protected void onDetach(@NonNull View view) {
      if (addCardContinueDialog != null) addCardContinueDialog.dismiss();
      super.onDetach(view);
   }

   @Override
   public OperationView<ResetSmartCardCommand> provideResetOperationView(FactoryResetDelegate factoryResetDelegate) {
      return FactoryResetOperationView.create(getContext(),
            factoryResetDelegate::factoryReset,
            () -> {},
            R.string.wallet_error_enter_pin_title,
            R.string.wallet_error_enter_pin_msg,
            R.string.retry,
            R.string.cancel,
            R.string.loading,
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
