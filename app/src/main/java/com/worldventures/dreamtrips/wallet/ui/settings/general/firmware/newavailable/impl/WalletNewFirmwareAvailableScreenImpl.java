package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.newavailable.impl;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.worldventures.core.utils.FileUtils;
import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.service.command.FetchFirmwareUpdateDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.RetryDialogErrorView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.newavailable.WalletNewFirmwareAvailablePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.newavailable.WalletNewFirmwareAvailableScreen;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.functions.Action1;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class WalletNewFirmwareAvailableScreenImpl extends WalletBaseController<WalletNewFirmwareAvailableScreen, WalletNewFirmwareAvailablePresenter> implements WalletNewFirmwareAvailableScreen {

   private View container;
   private View newDtAppRequired;
   private TextView updateDtApp;
   private TextView availableVersion;
   private TextView availableVersionSize;
   private TextView latestVersion;
   private TextView currentVersion;
   private TextView newVersionDescription;
   private Button downloadVersion;

   @Inject WalletNewFirmwareAvailablePresenter presenter;
   @Inject HttpErrorHandlingUtil httpErrorHandlingUtil;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      container = view.findViewById(R.id.container);
      container.setVisibility(INVISIBLE);
      newDtAppRequired = view.findViewById(R.id.new_dt_app_required);
      updateDtApp = view.findViewById(R.id.update_dt_app);
      updateDtApp.setOnClickListener(updateDtApp -> getPresenter().openMarket());
      availableVersion = view.findViewById(R.id.available_version);
      availableVersionSize = view.findViewById(R.id.available_version_size);
      latestVersion = view.findViewById(R.id.latest_version);
      currentVersion = view.findViewById(R.id.current_version);
      newVersionDescription = view.findViewById(R.id.new_version_description);
      newVersionDescription.setMovementMethod(new ScrollingMovementMethod());
      downloadVersion = view.findViewById(R.id.download_install_btn);
      downloadVersion.setOnClickListener(downloadBtn -> getPresenter().downloadButtonClicked());
      newVersionDescription.setMovementMethod(new ScrollingMovementMethod());
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_new_firmware_available, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
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
   public Context getViewContext() {
      return getContext();
   }

   @Override
   public OperationView<FetchFirmwareUpdateDataCommand> provideOperationView() {
      return new ComposableOperationView<>(new SimpleDialogProgressView<>(getContext(), R.string.loading, false),
            ErrorViewFactory.<FetchFirmwareUpdateDataCommand>builder()
                  .defaultErrorView(new RetryDialogErrorView<>(getContext(), R.string.error_something_went_wrong,
                        positiveFetchingAction, negativeFetchingAction))
                  .addProvider(new HttpErrorViewProvider<>(getContext(), httpErrorHandlingUtil,
                        positiveFetchingAction, negativeFetchingAction))
                  .build()
      );
   }

   private final Action1<FetchFirmwareUpdateDataCommand> positiveFetchingAction = cmd -> getPresenter().fetchFirmwareInfo();

   private final Action1<FetchFirmwareUpdateDataCommand> negativeFetchingAction = cmd -> getPresenter().goBack();

   @Override
   public WalletNewFirmwareAvailablePresenter getPresenter() {
      return presenter;
   }
}
