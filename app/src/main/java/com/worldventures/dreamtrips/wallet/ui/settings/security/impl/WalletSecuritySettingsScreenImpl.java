package com.worldventures.dreamtrips.wallet.ui.settings.security.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.security.clear.common.items.AutoClearSmartCardItemProvider;
import com.worldventures.dreamtrips.wallet.ui.settings.security.clear.common.items.DisableDefaultCardItemProvider;
import com.worldventures.dreamtrips.wallet.ui.settings.security.WalletSecuritySettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.WalletSecuritySettingsScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletSwitcher;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import rx.Observable;

public class WalletSecuritySettingsScreenImpl extends WalletBaseController<WalletSecuritySettingsScreen, WalletSecuritySettingsPresenter> implements WalletSecuritySettingsScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.lock_switcher) WalletSwitcher lockSwitcher;
   @InjectView(R.id.stealth_mode_switcher) WalletSwitcher stealthModeSwitcher;

   @InjectView(R.id.add_remove_pin) TextView addRemovePinLabel;
   @InjectView(R.id.disable_default_payment_card_label) TextView disableDefaultPaymentCardAfterLabel;
   @InjectView(R.id.auto_delete_cards_labels) TextView autoDeleteCardLabel;

   @InjectView(R.id.item_add_remove_pin) View addRemovePinItem;

   @InjectViews({R.id.item_reset_pin, R.id.item_reset_pin_label, R.id.item_reset_pin_sub_label})
   List<View> resetPinItemViews;

   @InjectViews({R.id.item_stealth_mode, R.id.item_offline_mode, R.id.item_disable_default_payment_card,
                      R.id.item_auto_delete_cards, R.id.item_reset_pin, R.id.item_add_remove_pin})
   List<View> toggleableItems;

   @Inject WalletSecuritySettingsPresenter presenter;
   @Inject DisableDefaultCardItemProvider disableDefaultCardItemProvider;
   @Inject AutoClearSmartCardItemProvider autoClearSmartCardItemProvider;

   private Observable<Boolean> lockSwitcherObservable;
   private Observable<Boolean> stealthModeSwitcherObservable;


   private MaterialDialog noConnectionDialog = null;

   public WalletSecuritySettingsScreenImpl() {
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      lockSwitcherObservable = observeCheckedChanges(lockSwitcher);
      stealthModeSwitcherObservable = observeCheckedChanges(stealthModeSwitcher);
   }

   private Observable<Boolean> observeCheckedChanges(CompoundButton compoundButton) {
      return RxCompoundButton.checkedChanges(compoundButton).skip(1);
   }

   protected void onNavigationClick() {
      getPresenter().goBack();
   }


   @OnClick(R.id.item_offline_mode)
   void onOfflineModeClick() {
      getPresenter().openOfflineModeScreen();
   }

   @OnClick(R.id.item_last_location)
   void onLastLocationClick() {
      getPresenter().openLostCardScreen();
   }

   @OnClick(R.id.item_disable_default_payment_card)
   void onDisableDefaultCardClick() {
      getPresenter().disableDefaultCardTimer();
   }

   @OnClick(R.id.item_reset_pin)
   void onResetPinClick() {
      getPresenter().resetPin();
   }

   @OnClick(R.id.item_auto_delete_cards)
   void onAutoDeleteCardsClick() {
      getPresenter().autoClearSmartCardClick();
   }

   @Override
   public void setAddRemovePinState(boolean isEnabled) {
      if (isEnabled) {
         addRemovePinLabel.setText(R.string.wallet_card_settings_remove_pin);
         addRemovePinItem.setOnClickListener(v -> prepareRemovePin());
         ButterKnife.apply(resetPinItemViews, (view, i) -> view.setEnabled(true));
      } else {
         addRemovePinLabel.setText(R.string.wallet_card_settings_add_pin);
         addRemovePinItem.setOnClickListener(v -> getPresenter().addPin());
         ButterKnife.apply(resetPinItemViews, (view, i) -> view.setEnabled(false));
      }
   }

   private void prepareRemovePin() {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_settings_prepare_remove_pin)
            .positiveText(R.string.wallet_label_yes)
            .onPositive((dialog, which) -> getPresenter().removePin())
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
   public void setLockToggleEnable(boolean enable) {
      lockSwitcher.setEnabled(enable);
   }

   @Override
   public boolean isLockToggleChecked() {
      return lockSwitcher.isChecked();
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
      return new DialogOperationScreen(getView());
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
   public List<View> getToggleableItems() {
      return toggleableItems;
   }

   @Override
   public Context getViewContext() {
      return getContext();
   }

   @Override
   protected void onDetach(@NonNull View view) {
      if (noConnectionDialog != null) noConnectionDialog.dismiss();
      super.onDetach(view);
   }

   @Override
   public WalletSecuritySettingsPresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_settings_security, viewGroup, false);
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
