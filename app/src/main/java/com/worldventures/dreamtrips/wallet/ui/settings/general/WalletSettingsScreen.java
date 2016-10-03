package com.worldventures.dreamtrips.wallet.ui.settings.general;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeView;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.common.provider.AutoClearSmartCardItemProvider;
import com.worldventures.dreamtrips.wallet.ui.settings.common.provider.DisableDefaultCardItemProvider;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;

public class WalletSettingsScreen extends WalletLinearLayout<WalletSettingsPresenter.Screen, WalletSettingsPresenter, WalletSettingsPath> implements WalletSettingsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.stealth_mode_switcher) SwitchCompat stealthModeSwitcher;
   @InjectView(R.id.lock_switcher) SwitchCompat lockSwitcher;
   @InjectView(R.id.test_connection_switcher) SwitchCompat testConnectionSwitcher;
   @InjectView(R.id.test_new_firmware_available) SwitchCompat testNewFirmwareAvailableSwitcher;
   @InjectView(R.id.test_firmware_is_compatible) SwitchCompat testFirmwareIsCompatibleSwitcher;
   @InjectView(R.id.test_firmware_is_enough_space) SwitchCompat testEnoughSpaceForFirmwareSwitcher;
   @InjectView(R.id.test_firmware_is_fail_install) SwitchCompat testFailInstallFirmwareSwitcher;

   @InjectView(R.id.badgeFirmwareUpdates) BadgeView badgeFirmwareUpdates;
   @InjectView(R.id.disable_default_payment_card_label) TextView disableDefaultPaymentCardAfterLabel;
   @InjectView(R.id.auto_delete_cards_labels) TextView autoDeleteCardLabel;
   @InjectView(R.id.firmware_version_label) TextView firmwareVersionLabel;

   private Observable<Boolean> stealthModeSwitcherObservable;
   private Observable<Boolean> lockSwitcherObservable;
   private Observable<Boolean> testConnectionObservable;
   private Observable<Boolean> testNewFirmwareAvailableObservable;
   private Observable<Boolean> testFirmwareIsCompatibleObservable;
   private Observable<Boolean> testEnoughSpaceObservable;
   private Observable<Boolean> testFailInstallationObservable;

   private final AutoClearSmartCardItemProvider autoClearSmartCardItemProvider = new AutoClearSmartCardItemProvider();
   private final DisableDefaultCardItemProvider disableDefaultCardItemProvider = new DisableDefaultCardItemProvider();

   public WalletSettingsScreen(Context context) {
      super(context);
   }

   public WalletSettingsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      if (isInEditMode()) return;
      lockSwitcherObservable = RxCompoundButton.checkedChanges(lockSwitcher);
      stealthModeSwitcherObservable = RxCompoundButton.checkedChanges(stealthModeSwitcher);
      testConnectionObservable = RxCompoundButton.checkedChanges(testConnectionSwitcher);
      testNewFirmwareAvailableObservable = RxCompoundButton.checkedChanges(testNewFirmwareAvailableSwitcher);
      testFirmwareIsCompatibleObservable = RxCompoundButton.checkedChanges(testFirmwareIsCompatibleSwitcher);
      testEnoughSpaceObservable = RxCompoundButton.checkedChanges(testEnoughSpaceForFirmwareSwitcher);
      testFailInstallationObservable = RxCompoundButton.checkedChanges(testFailInstallFirmwareSwitcher);
   }

   @NonNull
   @Override
   public WalletSettingsPresenter createPresenter() {
      return new WalletSettingsPresenter(getContext(), getInjector());
   }

   protected void onNavigationClick() {
      presenter.goBack();
   }

   @OnClick(R.id.item_reset_pin)
   protected void onResetPinClick() {
      presenter.resetPin();
   }

   @OnClick(R.id.item_disable_default_payment_card)
   protected void onDisableDefaultCardClick() {
      presenter.disableDefaultCardTimer();
   }

   @OnClick(R.id.item_auto_delete_cards)
   protected void onAutoDeleteCardsClick() {
      presenter.autoClearSmartCardClick();
   }

   @OnClick(R.id.item_firmware_updates)
   protected void onFirmwareUpdateClick() {
      presenter.firmwareUpdatesClick();
   }

   @OnClick(R.id.item_factory_reset)
   protected void onFactoryResetClick() {
      presenter.factoryResetClick();
   }

   @Override
   public void stealthModeStatus(boolean isEnabled) {
      stealthModeSwitcher.setChecked(isEnabled);
   }

   @Override
   public void lockStatus(boolean lock) {
      lockSwitcher.setChecked(lock);
   }

   @Override
   public void testConnection(boolean connected) {
      testConnectionSwitcher.setChecked(connected);
   }

   @Override
   public void disableDefaultPaymentValue(long millis) {
      disableDefaultPaymentCardAfterLabel.setText(disableDefaultCardItemProvider.provideTextByValue(millis));
   }

   @Override
   public void autoClearSmartCardValue(long millis) {
      autoDeleteCardLabel.setText(autoClearSmartCardItemProvider.provideTextByValue(millis));
   }

   @Override
   public void firmwareUpdateCount(int count) {
      badgeFirmwareUpdates.setText(String.valueOf(count));
   }

   @Override
   public void firmwareVersion(String version) {
      firmwareVersionLabel.setText(version);
   }

   @Override
   public void testNewFirmwareAvailable(boolean available) {
      testNewFirmwareAvailableSwitcher.setChecked(available);
   }

   @Override
   public void testFirmwareIsCompatible(boolean compatible) {
      testFirmwareIsCompatibleSwitcher.setChecked(compatible);
   }

   @Override
   public void testEnoughSpaceForFirmware(boolean compatible) {
      testEnoughSpaceForFirmwareSwitcher.setChecked(compatible);
   }

   @Override
   public void testFailInstallation(boolean failInstall) {
      testFailInstallFirmwareSwitcher.setChecked(failInstall);
   }

   @Override
   public Observable<Boolean> stealthModeStatus() {
      return stealthModeSwitcherObservable;
   }

   @Override
   public Observable<Boolean> lockStatus() {
      return lockSwitcherObservable;
   }

   @Override
   public Observable<Boolean> testConnection() {
      return testConnectionObservable;
   }

   @Override
   public Observable<Boolean> testNewFirmwareAvailable() {
      return testNewFirmwareAvailableObservable;
   }

   @Override
   public Observable<Boolean> testFirmwareIsCompatible() {
      return testFirmwareIsCompatibleObservable;
   }

   @Override
   public Observable<Boolean> testEnoughSpaceForFirmware() {
      return testEnoughSpaceObservable;
   }

   @Override
   public Observable<Boolean> testFailInstallation() {
      return testFailInstallationObservable;
   }

   @Override
   public void showFirmwareVersion() {
      badgeFirmwareUpdates.setVisibility(GONE);
      firmwareVersionLabel.setVisibility(VISIBLE);
   }

   @Override
   public void showFirmwareBadge() {
      firmwareVersionLabel.setVisibility(GONE);
      badgeFirmwareUpdates.setVisibility(VISIBLE);
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }
}
