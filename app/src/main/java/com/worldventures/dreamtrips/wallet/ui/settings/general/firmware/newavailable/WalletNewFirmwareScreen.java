package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.newavailable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;
import com.worldventures.dreamtrips.core.utils.FileUtils;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.service.command.FetchFirmwareUpdateDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.RetryDialogErrorView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WalletNewFirmwareScreen
      extends WalletLinearLayout<WalletNewFirmwareAvailablePresenter.Screen, WalletNewFirmwareAvailablePresenter, WalletNewFirmwareAvailablePath>
      implements WalletNewFirmwareAvailablePresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.container) View container;
   @InjectView(R.id.new_dt_app_required) View newDtAppRequired;
   @InjectView(R.id.update_dt_app) View updateDtApp;
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
      supportConnectionStatusLabel(false);
      super.onFinishInflate();
      newVersionDescription.setMovementMethod(new ScrollingMovementMethod());
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      container.setVisibility(INVISIBLE);
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

   @OnClick(R.id.update_dt_app)
   protected void updateDtApp() {
      getPresenter().openMarket();
   }

   @OnClick(R.id.download_install_btn)
   protected void onAttachmentButtonClicked() {
      getPresenter().downloadButtonClicked();
   }

   @Override
   public void currentFirmwareInfo(@Nullable SmartCardFirmware version, FirmwareInfo firmwareInfo, boolean isCompatible) {
      currentVersion.setText(version == null ? "" : getString(R.string.wallet_settings_version_current, version.nordicAppVersion()));

      availableFirmwareInfo(firmwareInfo);
      requiredLatestDtAppVersion(isCompatible);

      container.setVisibility(VISIBLE);
   }

   private void availableFirmwareInfo(FirmwareInfo firmwareInfo) {
      availableVersion.setText(getResources().getString(R.string.wallet_settings_version, firmwareInfo.firmwareVersion()));
      String size = FileUtils.byteCountToDisplaySize(firmwareInfo.fileSize());
      availableVersionSize.setText(getResources().getString(R.string.wallet_settings_update_size, size)); // convert to KB
      newVersionDescription.setText(firmwareInfo.releaseNotes());

      latestVersion.setText(getResources().getString(R.string.wallet_settings_version_latest, firmwareInfo.firmwareVersion()));
   }

   public void requiredLatestDtAppVersion(boolean isCompatible) {
      newDtAppRequired.setVisibility(isCompatible ? GONE : VISIBLE);
      updateDtApp.setVisibility(isCompatible ? GONE : VISIBLE);
      downloadVersion.setEnabled(isCompatible);
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

   @Override
   public OperationView<FetchFirmwareUpdateDataCommand> provideOperationView() {
      return new ComposableOperationView<>(new SimpleDialogProgressView<>(getContext(), R.string.loading, false),
            ErrorViewFactory.<FetchFirmwareUpdateDataCommand>builder()
                  .defaultErrorView(new RetryDialogErrorView<>(getContext(), R.string.error_something_went_wrong,
                        command -> getPresenter().fetchFirmwareInfo(),
                        command -> getPresenter().goBack()))
                  .addProvider(new HttpErrorViewProvider<>(getContext(), presenter.httpErrorHandlingUtil(),
                        command -> getPresenter().fetchFirmwareInfo(),
                        command -> getPresenter().goBack()))
                  .build()
      );
   }
}
