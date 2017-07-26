package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.impl;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.WalletInstallFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.WalletInstallFirmwareScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.functions.Action1;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class WalletInstallFirmwareScreenImpl extends WalletBaseController<WalletInstallFirmwareScreen, WalletInstallFirmwarePresenter> implements WalletInstallFirmwareScreen {

   private static final String KEY_INSTALL_STARTED = "key_install_started";

   @InjectView(R.id.firmware_install_progress) WalletProgressWidget installProgress;
   @InjectView(R.id.progressStatusLabel) TextView progressStatusLabel;
   @InjectView(R.id.install_step) TextView installStep;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   @Inject WalletInstallFirmwarePresenter presenter;

   private boolean started;
   private Dialog errorDialog;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
      installProgress.start();
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
   public OperationScreen provideOperationDelegate() { return new DialogOperationScreen(getView()); }

   @Override
   public void showProgress(@Nullable String text) {
      installProgress.setVisibility(VISIBLE);
   }

   @Override
   public void hideProgress() {
      installProgress.setVisibility(INVISIBLE);
   }

   @Override
   public Context context() {
      // redundant method from Operation Screen interface
      return getContext();
   }

   @Override
   public void showError(String msg, @Nullable Action1 action) {
      errorDialog = new MaterialDialog.Builder(getContext())
            .title(R.string.wallet_firmware_install_error_alert_title)
            .content(createDialogContentText())
            .positiveText(R.string.wallet_firmware_install_error_retry_action)
            .onPositive((dialog, which) -> getPresenter().retry())
            .negativeText(R.string.wallet_firmware_install_error_cancel_action)
            .onNegative((dialog, which) -> getPresenter().cancelReinstall())
            .cancelable(false)
            .build();
      errorDialog.show();
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
   protected void onSaveInstanceState(@NonNull Bundle outState) {
      outState.putBoolean(KEY_INSTALL_STARTED,  started);
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
