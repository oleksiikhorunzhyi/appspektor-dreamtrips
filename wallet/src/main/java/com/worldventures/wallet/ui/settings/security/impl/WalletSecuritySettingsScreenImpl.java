package com.worldventures.wallet.ui.settings.security.impl;

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
import com.worldventures.wallet.R;
import com.worldventures.wallet.service.command.SetLockStateCommand;
import com.worldventures.wallet.service.command.SetStealthModeCommand;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.wallet.ui.common.helper2.error.SCConnectionErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.error.SmartCardErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.wallet.ui.settings.security.WalletSecuritySettingsPresenter;
import com.worldventures.wallet.ui.settings.security.WalletSecuritySettingsScreen;
import com.worldventures.wallet.ui.settings.security.clear.common.items.AutoClearSmartCardItemProvider;
import com.worldventures.wallet.ui.settings.security.clear.common.items.DisableDefaultCardItemProvider;
import com.worldventures.wallet.ui.widget.WalletSwitcher;
import com.worldventures.wallet.util.SmartCardConnectException;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public class WalletSecuritySettingsScreenImpl extends WalletBaseController<WalletSecuritySettingsScreen, WalletSecuritySettingsPresenter> implements WalletSecuritySettingsScreen {

   @Inject WalletSecuritySettingsPresenter presenter;
   @Inject DisableDefaultCardItemProvider disableDefaultCardItemProvider;
   @Inject AutoClearSmartCardItemProvider autoClearSmartCardItemProvider;

   private WalletSwitcher lockSwitcher;
   private WalletSwitcher stealthModeSwitcher;
   private TextView addRemovePinLabel;
   private TextView disableDefaultPaymentCardAfterLabel;
   private TextView autoDeleteCardLabel;
   private View addRemovePinItem;
   private List<View> resetPinItemViews;
   private List<View> toggleableItems;
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
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      lockSwitcher = view.findViewById(R.id.lock_switcher);
      lockSwitcherObservable = observeCheckedChanges(lockSwitcher);
      stealthModeSwitcher = view.findViewById(R.id.stealth_mode_switcher);
      stealthModeSwitcherObservable = observeCheckedChanges(stealthModeSwitcher);
      addRemovePinLabel = view.findViewById(R.id.add_remove_pin);
      disableDefaultPaymentCardAfterLabel = view.findViewById(R.id.disable_default_payment_card_label);
      autoDeleteCardLabel = view.findViewById(R.id.auto_delete_cards_labels);
      addRemovePinItem = view.findViewById(R.id.item_add_remove_pin);
      final View itemResetPinView = view.findViewById(R.id.item_reset_pin);
      itemResetPinView.setOnClickListener(resetPin -> getPresenter().resetPin());
      final View itemOfflineMode = view.findViewById(R.id.item_offline_mode);
      itemOfflineMode.setOnClickListener(offlineMode -> getPresenter().openOfflineModeScreen());
      final View itemDisableDefaultView = view.findViewById(R.id.item_disable_default_payment_card);
      itemDisableDefaultView.setOnClickListener(disableDefault -> getPresenter().disableDefaultCardTimer());
      final View itemAutoClearView = view.findViewById(R.id.item_auto_delete_cards);
      itemAutoClearView.setOnClickListener(autoClear -> getPresenter().autoClearSmartCardClick());
      resetPinItemViews = Arrays.asList(itemResetPinView, view.findViewById(R.id.item_reset_pin_label),
            view.findViewById(R.id.item_reset_pin_sub_label));
      toggleableItems = Arrays.asList(addRemovePinItem, itemOfflineMode, itemResetPinView, itemDisableDefaultView,
            itemAutoClearView, view.findViewById(R.id.item_stealth_mode));
      final View itemLastLocationView = view.findViewById(R.id.item_last_location);
      itemLastLocationView.setOnClickListener(lastLocation -> getPresenter().openLostCardScreen());
   }

   private Observable<Boolean> observeCheckedChanges(CompoundButton compoundButton) {
      return RxCompoundButton.checkedChanges(compoundButton).skip(1);
   }

   protected void onNavigationClick() {
      getPresenter().goBack();
   }

   @Override
   public void setAddRemovePinState(boolean isEnabled) {
      addRemovePinLabel.setText(isEnabled
            ? R.string.wallet_card_settings_remove_pin
            : R.string.wallet_card_settings_add_pin);
      addRemovePinItem.setOnClickListener(v -> {
         if (isEnabled) {
            prepareRemovePin();
         } else {
            getPresenter().addPin();
         }
      });
      for (View view : resetPinItemViews) {
         view.setEnabled(isEnabled);
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
   public void showSCNonConnectionDialog() {
      if (noConnectionDialog == null) {
         noConnectionDialog = new MaterialDialog.Builder(getContext())
               .title(R.string.wallet_card_settings_cant_connected)
               .content(R.string.wallet_card_settings_message_cant_connected)
               .positiveText(R.string.wallet_ok)
               .build();
      }
      if (!noConnectionDialog.isShowing()) {
         noConnectionDialog.show();
      }
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
   public OperationView<SetLockStateCommand> provideOperationSeLockState() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_loading, false),
            ErrorViewFactory.<SetLockStateCommand>builder()
                  .addProvider(new SimpleDialogErrorViewProvider<>(
                        getContext(),
                        SmartCardConnectException.class,
                        R.string.wallet_smartcard_connection_error,
                        command -> getPresenter().lockStatusFailed()))
                  .build()
      );
   }

   @Override
   public OperationView<SetStealthModeCommand> provideOperationSetStealthMode() {
      return new ComposableOperationView<>(
            ErrorViewFactory.<SetStealthModeCommand>builder()
                  .addProvider(new SCConnectionErrorViewProvider<>(getContext()))
                  .addProvider(new SmartCardErrorViewProvider<>(getContext()))
                  .build()
      );
   }

   @Override
   protected void onDetach(@NonNull View view) {
      if (noConnectionDialog != null) {
         noConnectionDialog.dismiss();
      }
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
