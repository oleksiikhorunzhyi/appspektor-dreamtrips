package com.worldventures.dreamtrips.wallet.ui.settings.general;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeView;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.common.provider.AutoClearSmartCardItemProvider;
import com.worldventures.dreamtrips.wallet.ui.settings.common.provider.DisableDefaultCardItemProvider;

import java.util.Date;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;

public class WalletSettingsScreen extends WalletLinearLayout<WalletSettingsPresenter.Screen, WalletSettingsPresenter, WalletSettingsPath> implements WalletSettingsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.status) TextView status;

   @InjectView(R.id.offline_mode_switcher) SwitchCompat offlineModeSwitcher;
   @InjectView(R.id.lock_switcher) SwitchCompat lockSwitcher;
   @InjectView(R.id.stealth_mode_switcher) SwitchCompat stealthModeSwitcher;
   @InjectView(R.id.alert_connection_switcher) SwitchCompat alertConnectionSwitcher;

   @InjectView(R.id.test_connection_switcher) SwitchCompat testConnectionSwitcher;
   @InjectView(R.id.test_firmware_is_fail_install) SwitchCompat testFailInstallFirmwareSwitcher;

   @InjectView(R.id.badgeFirmwareUpdates) BadgeView badgeFirmwareUpdates;
   @InjectView(R.id.disable_default_payment_card_label) TextView disableDefaultPaymentCardAfterLabel;
   @InjectView(R.id.auto_delete_cards_labels) TextView autoDeleteCardLabel;
   @InjectView(R.id.firmware_version_label) TextView firmwareVersionLabel;
   @InjectView(R.id.settings_section) ViewGroup settingsSection;

   private Observable<Boolean> offlineModeSwitcherObservable;
   private Observable<Boolean> lockSwitcherObservable;
   private Observable<Boolean> stealthModeSwitcherObservable;
   private Observable<Boolean> alertConnectionSwitcherObservable;

   private Observable<Boolean> testConnectionObservable;
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
      offlineModeSwitcherObservable = RxCompoundButton.checkedChanges(offlineModeSwitcher);
      lockSwitcherObservable = RxCompoundButton.checkedChanges(lockSwitcher);
      stealthModeSwitcherObservable = RxCompoundButton.checkedChanges(stealthModeSwitcher);
      alertConnectionSwitcherObservable = RxCompoundButton.checkedChanges(alertConnectionSwitcher);

      testConnectionObservable = RxCompoundButton.checkedChanges(testConnectionSwitcher);
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

   @OnClick(R.id.item_smartcard_profile)
   void onProfileClick() {
      presenter.smartCardProfileClick();

   }

   @OnClick(R.id.item_about)
   void onAboutClick() {
      presenter.openAboutScreen();
   }

   @OnClick(R.id.item_battery_alert)
   void onBatteryAlertClick() {
   }

   @OnClick(R.id.item_last_location)
   void onLastLocationClick() {
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

   @OnClick(R.id.item_firmware_updates)
   void onFirmwareUpdateClick() {
      presenter.firmwareUpdatesClick();
   }

   @OnClick(R.id.item_setup_new_sc)
   void onSetupNewSmartCardClick() {
   }

   @OnClick(R.id.item_factory_reset)
   void onFactoryResetClick() {
      presenter.factoryResetClick();
   }

   @OnClick(R.id.item_restart_sc)
   void onRestartSmartCard() {
      new MaterialDialog.Builder(getContext())
            .title(R.string.wallet_card_settings_restart_sc)
            .content(R.string.wallet_card_settings_are_you_sure)
            .positiveText(R.string.wallet_card_settings_restart)
            .negativeText(R.string.cancel)
            .onPositive(((dialog, which) -> presenter.confirmResetSmartCard()))
            .build()
            .show();
   }

   @OnClick(R.id.item_unpair_sc)
   void onUnpairSmartCardClick() {
   }

   @Override
   public void smartCardGeneralStatus(@Nullable SmartCardFirmware version, int batteryLevel, Date lastSync) {
      final StringBuilder builder = new StringBuilder();
      if (version != null) {
         builder.append(getString(R.string.wallet_card_settings_version, version.nordicAppVersion())).append("\n");
      }
      builder.append(getString(R.string.wallet_card_settings_battery_level, batteryLevel)).append("\n")
      //            .append(getString(R.string.wallet_card_settings_last_sync, formattedLastSync))
      ;

      status.setText(builder.toString());
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
   public void disableDefaultPaymentValue(long minutes) {
      disableDefaultPaymentCardAfterLabel.setText(disableDefaultCardItemProvider.provideTextByValue(minutes));
   }

   @Override
   public void autoClearSmartCardValue(long minutes) {
      autoDeleteCardLabel.setText(autoClearSmartCardItemProvider.provideTextByValue(minutes));
   }

   @Override
   public void firmwareUpdateCount(int count) {
      badgeFirmwareUpdates.setText(String.valueOf(count));
   }

   @Override
   public void firmwareVersion(@Nullable SmartCardFirmware version) {
      firmwareVersionLabel.setText(version == null ? "" : version.nordicAppVersion());
   }

   @Override
   public void testFailInstallation(boolean failInstall) {
      testFailInstallFirmwareSwitcher.setChecked(failInstall);
   }

   @Override
   public Observable<Boolean> offlineMode() {
      return offlineModeSwitcherObservable;
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
   public Observable<Boolean> alertConnection() {
      return alertConnectionSwitcherObservable;
   }

   @Override
   public Observable<Boolean> testConnection() {
      return testConnectionObservable;
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

   @Override
   public void testSectionEnabled(boolean enabled) {
      settingsSection.setVisibility(enabled ? VISIBLE : GONE);
   }
}
