package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.impl;


import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.firmware.command.InstallFirmwareCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.CustomDialogErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SCConnectionErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.WalletProgressView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.WalletInstallFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.WalletInstallFirmwareScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.functions.Action1;

public class WalletInstallFirmwareScreenImpl extends WalletBaseController<WalletInstallFirmwareScreen, WalletInstallFirmwarePresenter> implements WalletInstallFirmwareScreen {

   private static final String KEY_INSTALL_STARTED = "key_install_started";

   @Inject WalletInstallFirmwarePresenter presenter;
   @Inject HttpErrorHandlingUtil httpErrorHandlingUtil;

   private WalletProgressWidget installProgress;
   private TextView progressStatusLabel;
   private TextView installStep;
   private MaterialDialog errorDialog;
   private boolean started;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
      installProgress = view.findViewById(R.id.firmware_install_progress);
      installProgress.start();
      progressStatusLabel = view.findViewById(R.id.progressStatusLabel);
      installStep = view.findViewById(R.id.install_step);
      setupCustomErrorDialog();
   }

   private void setupCustomErrorDialog() {
      errorDialog = new MaterialDialog.Builder(getContext())
            .title(R.string.wallet_firmware_install_error_alert_title)
            .content(createDialogContentText())
            .positiveText(R.string.wallet_firmware_install_error_retry_action)
            .onPositive((dialog, which) -> getPresenter().retry())
            .negativeText(R.string.wallet_firmware_install_error_cancel_action)
            .onNegative((dialog, which) -> getPresenter().cancelReinstall())
            .cancelable(false)
            .build();
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_install_firmware, viewGroup, false);
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
   protected void onDetach(@NonNull View view) {
      if (errorDialog != null) errorDialog.dismiss();
      super.onDetach(view);
   }

   @Override
   public WalletInstallFirmwarePresenter getPresenter() {
      return presenter;
   }

   @Override
   public void showInstallingStatus(int currentStep, int totalSteps, int progress) {
      progressStatusLabel.setText(String.format("%d%%", progress));
      installStep.setText(getString(R.string.wallet_firmware_install_sub_text, currentStep, totalSteps));
   }

   @Override
   public void setInstallStarted(boolean started) {
      this.started = started;
   }

   @Override
   public boolean isInstallStarted() {
      return started;
   }

   @Override
   public OperationView<InstallFirmwareCommand> provideOperationInstall() {
      return new ComposableOperationView<>(
            new WalletProgressView<>(installProgress),
            ErrorViewFactory.<InstallFirmwareCommand>builder()
                  .addProvider(new HttpErrorViewProvider<>(getContext(), httpErrorHandlingUtil,
                        positiveInstallingAction, negativeInstallingAction))
                  .addProvider(new SCConnectionErrorViewProvider<>(getContext(),
                        positiveInstallingAction, negativeInstallingAction))
                  .addProvider(new CustomDialogErrorViewProvider<>(errorDialog, Throwable.class))
                  .build()
      );
   }

   private final Action1<InstallFirmwareCommand> positiveInstallingAction = cmd -> getPresenter().install();

   private final Action1<InstallFirmwareCommand> negativeInstallingAction = cmd -> getPresenter().cancelReinstall();

   @Override
   protected void onSaveInstanceState(@NonNull Bundle outState) {
      outState.putBoolean(KEY_INSTALL_STARTED, started);
      super.onSaveInstanceState(outState);
   }

   @Override
   protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      started = savedInstanceState.getBoolean(KEY_INSTALL_STARTED, false);
   }

   private CharSequence createDialogContentText() {
      SpannableString supportPhoneNumber = new SpannableString(getString(R.string.wallet_firmware_install_customer_support_phone_number));
      supportPhoneNumber.setSpan(new StyleSpan(Typeface.BOLD), 0, supportPhoneNumber.length(), 0);
      supportPhoneNumber.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.wallet_alert_phone_number_color)), 0, supportPhoneNumber
            .length(), 0);
      Linkify.addLinks(supportPhoneNumber, Linkify.PHONE_NUMBERS);

      return new SpannableStringBuilder()
            .append(getString(R.string.wallet_firmware_install_error_alert_content))
            .append("\n\n")
            .append(getString(R.string.wallet_firmware_install_error_alert_content_customer_support))
            .append("\n")
            .append(supportPhoneNumber);
   }
}
