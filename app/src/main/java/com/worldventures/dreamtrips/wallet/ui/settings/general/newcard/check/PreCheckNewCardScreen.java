package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.check;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletCheckWidget;

import butterknife.InjectView;
import butterknife.OnClick;

public class PreCheckNewCardScreen extends WalletLinearLayout<PreCheckNewCardPresenter.Screen, PreCheckNewCardPresenter, PreCheckNewCardPath> implements PreCheckNewCardPresenter.Screen {

   private MaterialDialog addCardContinueDialog = null;

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.btn_next) Button nextBtn;
   @InjectView(R.id.check_widget_bluetooth) WalletCheckWidget checkWidgetBluetooth;
   @InjectView(R.id.check_widget_connection) WalletCheckWidget checkWidgetConnection;

   public PreCheckNewCardScreen(Context context) {
      super(context);
   }

   public PreCheckNewCardScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public PreCheckNewCardPresenter createPresenter() {
      return new PreCheckNewCardPresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   protected void onFinishInflate() {
      supportConnectionStatusLabel(false);
      super.onFinishInflate();
      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
   }

   @Override
   public void showAddCardContinueDialog(String scId) {
      if (addCardContinueDialog == null) {
         addCardContinueDialog = new MaterialDialog.Builder(getContext())
               .content(Html.fromHtml(getString(R.string.wallet_new_card_pre_install_confirm_message, scId)))
               .positiveText(R.string.wallet_continue_label)
               .onPositive((dialog, which) -> presenter.navigateNext())
               .onNegative((dialog, which) -> presenter.goBack())
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
   protected void onDetachedFromWindow() {
      if (addCardContinueDialog != null) addCardContinueDialog.dismiss();
      super.onDetachedFromWindow();
   }
}
