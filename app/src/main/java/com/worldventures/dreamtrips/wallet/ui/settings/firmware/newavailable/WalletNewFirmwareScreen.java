package com.worldventures.dreamtrips.wallet.ui.settings.firmware.newavailable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareInfo;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import org.apache.commons.io.FileUtils;

import butterknife.InjectView;
import butterknife.OnClick;

public class WalletNewFirmwareScreen
      extends WalletFrameLayout<WalletNewFirmwareAvailablePresenter.Screen, WalletNewFirmwareAvailablePresenter, WalletNewFirmwareAvailablePath>
      implements WalletNewFirmwareAvailablePresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.new_dt_app_required) TextView newDtAppRequired;
   @InjectView(R.id.btn_update_dt) Button updateDtApp;
   @InjectView(R.id.available_version) TextView availableVersion;
   @InjectView(R.id.available_version_size) TextView availableVersionSize;
   @InjectView(R.id.latest_version) TextView latestVersion;
   @InjectView(R.id.current_version) TextView currentVersion;
   @InjectView(R.id.new_version_description) TextView newVersionDescription;
   @InjectView(R.id.download_install_btn) Button downloadVersion;

   public WalletNewFirmwareScreen(Context context) {
      super(context);
   }

   public WalletNewFirmwareScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
   }

   @NonNull
   @Override
   public WalletNewFirmwareAvailablePresenter createPresenter() {
      return new WalletNewFirmwareAvailablePresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @OnClick(R.id.btn_update_dt)
   protected void updateDtApp() {
      getPresenter().openMarket();
   }

   @OnClick(R.id.download_install_btn)
   protected void onAttachmentButtonClicked() {
      getPresenter().downloadButtonClicked();
   }

   @Override
   public void requiredLatestDtAppVersion() {
      newDtAppRequired.setVisibility(VISIBLE);
      updateDtApp.setVisibility(VISIBLE);
      downloadVersion.setEnabled(false);
   }

   @Override
   public void availableFirmwareInfo(FirmwareInfo firmwareInfo) {
      availableVersion.setText(getResources().getString(R.string.wallet_settings_version, firmwareInfo.versionName()));
      String size = FileUtils.byteCountToDisplaySize(firmwareInfo.byteSize());
      availableVersionSize.setText(getResources().getString(R.string.wallet_settings_update_size, size)); // convert to KB
      newVersionDescription.setText(firmwareInfo.releaseNotes());

      latestVersion.setText(getResources().getString(R.string.wallet_settings_version_latest, firmwareInfo.versionName()));
   }

   @Override
   public void currentFirmwareInfo(String version) {
      currentVersion.setText(getResources().getString(R.string.wallet_settings_version_current, version));
   }

   @Override
   public void insufficientSpace(long missingByteSpace) {
      String size = FileUtils.byteCountToDisplaySize(missingByteSpace);
      new AlertDialog.Builder(getContext())
            .setTitle(R.string.wallet_firmware_space_alert_title)
            .setMessage(getContext().getString(R.string.wallet_firmware_space_alert_description, size))
            .setPositiveButton(R.string.wallet_firmware_space_alert_settings_action, (dialogInterface, i) -> getPresenter()
                  .openSettings())
            .setNegativeButton(R.string.wallet_firmware_space_alert_cancel_action, null)
            .show();
   }
}