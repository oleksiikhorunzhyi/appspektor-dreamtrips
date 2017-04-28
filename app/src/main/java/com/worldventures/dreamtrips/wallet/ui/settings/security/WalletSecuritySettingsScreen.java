package com.worldventures.dreamtrips.wallet.ui.settings.security;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.common.provider.AutoClearSmartCardItemProvider;
import com.worldventures.dreamtrips.wallet.ui.settings.common.provider.DisableDefaultCardItemProvider;
import com.worldventures.dreamtrips.wallet.ui.settings.general.WalletGeneralSettingsPath;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletSwitcher;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import rx.Observable;

public class WalletSecuritySettingsScreen extends WalletLinearLayout<WalletSecuritySettingsPresenter.Screen, WalletSecuritySettingsPresenter, WalletGeneralSettingsPath> implements WalletSecuritySettingsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.lock_switcher) WalletSwitcher lockSwitcher;
   @InjectView(R.id.stealth_mode_switcher) WalletSwitcher stealthModeSwitcher;

   @InjectView(R.id.add_remove_pin) TextView addRemovePinLabel;
   @InjectView(R.id.disable_default_payment_card_label) TextView disableDefaultPaymentCardAfterLabel;
   @InjectView(R.id.auto_delete_cards_labels) TextView autoDeleteCardLabel;

   @InjectView(R.id.item_add_remove_pin) View addRemovePinItem;

   @InjectViews({R.id.item_reset_pin, R.id.item_reset_pin_label, R.id.item_reset_pin_sub_label})
   List<View> resetPinItemViews;

   private Observable<Boolean> lockSwitcherObservable;
   private Observable<Boolean> stealthModeSwitcherObservable;

   private final AutoClearSmartCardItemProvider autoClearSmartCardItemProvider;
   private final DisableDefaultCardItemProvider disableDefaultCardItemProvider;

   private MaterialDialog noConnectionDialog = null;

   public WalletSecuritySettingsScreen(Context context) {
      this(context, null);
   }

   public WalletSecuritySettingsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
      autoClearSmartCardItemProvider = new AutoClearSmartCardItemProvider(context);
      disableDefaultCardItemProvider = new DisableDefaultCardItemProvider(context);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      if (isInEditMode()) return;
      lockSwitcherObservable = observeCheckedChanges(lockSwitcher);
      stealthModeSwitcherObservable = observeCheckedChanges(stealthModeSwitcher);
   }

   private Observable<Boolean> observeCheckedChanges(CompoundButton compoundButton) {
      return RxCompoundButton.checkedChanges(compoundButton).skip(1);
   }

   @NonNull
   @Override
   public WalletSecuritySettingsPresenter createPresenter() {
      return new WalletSecuritySettingsPresenter(getContext(), getInjector());
   }

   protected void onNavigationClick() {
      presenter.goBack();
   }


   @OnClick(R.id.item_offline_mode)
   void onOfflineModeClick() {
      presenter.openOfflineModeScreen();
   }

   @OnClick(R.id.item_last_location)
   void onLastLocationClick() {
      presenter.openLostCardScreen();
   }

   @OnClick(R.id.item_disable_default_payment_card)
   void onDisableDefaultCardClick() {
      presenter.disableDefaultCardTimer();
   }

   @OnClick(R.id.item_reset_pin)
   void onResetPinClick() {
      presenter.resetPin();
   }

   @OnClick(R.id.item_auto_delete_cards)
   void onAutoDeleteCardsClick() {
      presenter.autoClearSmartCardClick();
   }

   @Override
   public void setAddRemovePinState(boolean isEnabled) {
      if (isEnabled) {
         addRemovePinLabel.setText(R.string.wallet_card_settings_remove_pin);
         addRemovePinItem.setOnClickListener(v -> prepareRemovePin());
         ButterKnife.apply(resetPinItemViews, (view, i) -> view.setEnabled(true));
      } else {
         addRemovePinLabel.setText(R.string.wallet_card_settings_add_pin);
         addRemovePinItem.setOnClickListener(v -> presenter.addPin());
         ButterKnife.apply(resetPinItemViews, (view, i) -> view.setEnabled(false));
      }
   }

   private void prepareRemovePin() {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_settings_prepare_remove_pin)
            .positiveText(R.string.wallet_label_yes)
            .onPositive((dialog, which) -> presenter.removePin())
            .negativeText(R.string.wallet_label_no)
            .onNegative((dialog, which) -> dialog.dismiss())
            .build()
            .show();
   }

   @Override
   public void stealthModeStatus(boolean isEnabled) {
      stealthModeSwitcher.setCheckedWithoutNotify(isEnabled);
   }

   @Override
   public void lockStatus(boolean lock) {
      lockSwitcher.setCheckedWithoutNotify(lock);
   }

   @Override
   public void disableDefaultPaymentValue(long minutes) {
      disableDefaultPaymentCardAfterLabel.setText(disableDefaultCardItemProvider.provideTextByValue(minutes));
   }

   @Override
   public void autoClearSmartCardValue(long minutes) {
      autoDeleteCardLabel.setText(autoClearSmartCardItemProvider.provideTextByValue(minutes));
   }


   @Override
   public Observable<Boolean> lockStatus() {
      return lockSwitcherObservable;
   }

   @Override
   public Observable<Boolean> stealthModeStatus() {
      return stealthModeSwitcherObservable;
   }


   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @Override
   public void showSCNonConnectionDialog() {
      if (noConnectionDialog == null) {
         noConnectionDialog = new MaterialDialog.Builder(getContext())
               .title(R.string.wallet_card_settings_cant_connected)
               .content(R.string.wallet_card_settings_message_cant_connected)
               .positiveText(R.string.ok)
               .build();
      }
      if (!noConnectionDialog.isShowing()) noConnectionDialog.show();
   }

   @Override
   protected void onDetachedFromWindow() {
      if (noConnectionDialog != null) noConnectionDialog.dismiss();
      super.onDetachedFromWindow();
   }
}
