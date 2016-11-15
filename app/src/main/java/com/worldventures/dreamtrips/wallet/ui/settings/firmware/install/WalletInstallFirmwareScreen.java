package com.worldventures.dreamtrips.wallet.ui.settings.firmware.install;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;

import butterknife.InjectView;
import rx.functions.Action1;

public class WalletInstallFirmwareScreen extends WalletLinearLayout<WalletInstallFirmwarePresenter.Screen, WalletInstallFirmwarePresenter, WalletInstallFirmwarePath>
      implements WalletInstallFirmwarePresenter.Screen {

   @InjectView(R.id.firmware_install_progress) WalletProgressWidget installProgress;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   public WalletInstallFirmwareScreen(Context context) {
      super(context);
   }

   public WalletInstallFirmwareScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public WalletInstallFirmwarePresenter createPresenter() {
      return new WalletInstallFirmwarePresenter(getContext(), getInjector(), getPath().firmwareData);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
      installProgress.start();
      supportConnectionStatusLabel(false);
   }

   @Override
   public OperationScreen provideOperationDelegate() { return new DialogOperationScreen(this); }

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
      new MaterialDialog.Builder(getContext())
            .title(R.string.wallet_firmware_install_error_alert_title)
            .content(createDialogContentText())
            .positiveText(R.string.wallet_firmware_install_error_retry_action)
            .onPositive((dialog, which) -> getPresenter().retry())
            .negativeText(R.string.wallet_firmware_install_error_cancel_action)
            .onNegative((dialog, which) -> getPresenter().goToDashboard())
            .cancelable(false)
            .show();
   }

   private CharSequence createDialogContentText() {
      SpannableString supportPhoneNumber = new SpannableString(getString(R.string.wallet_firmware_install_customer_support_phone_number));
      supportPhoneNumber.setSpan(new StyleSpan(Typeface.BOLD), 0, supportPhoneNumber.length(), 0);
      supportPhoneNumber.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.wallet_alert_phone_number_color)), 0, supportPhoneNumber.length(), 0);
      Linkify.addLinks(supportPhoneNumber, Linkify.PHONE_NUMBERS);

      return new SpannableStringBuilder()
            .append(getString(R.string.wallet_firmware_install_error_alert_content))
            .append("\n\n")
            .append(getString(R.string.wallet_firmware_install_error_alert_content_customer_support))
            .append("\n")
            .append(supportPhoneNumber);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }
}