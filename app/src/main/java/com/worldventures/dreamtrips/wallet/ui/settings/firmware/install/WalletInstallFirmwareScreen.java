package com.worldventures.dreamtrips.wallet.ui.settings.firmware.install;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;

import butterknife.InjectView;

public class WalletInstallFirmwareScreen extends WalletFrameLayout<WalletInstallFirmwarePresenter.Screen, WalletInstallFirmwarePresenter, WalletInstallFirmwarePath>
      implements WalletInstallFirmwarePresenter.Screen {

   @InjectView(R.id.firmware_install_progress) WalletProgressWidget installProgress;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   public WalletInstallFirmwareScreen(Context context) {
      super(context);
   }

   public WalletInstallFirmwareScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public WalletInstallFirmwarePresenter createPresenter() {
      return new WalletInstallFirmwarePresenter(getContext(), getInjector(), getPath().filePath());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
      installProgress.start();
   }

   @Override
   public OperationScreen provideOperationDelegate() { return new DialogOperationScreen(this); }

   @Override
   public void showError() {
      installProgress.setVisibility(INVISIBLE);
      new MaterialDialog.Builder(getContext())
            .title(R.string.wallet_firmware_install_error_text)
            .content(R.string.wallet_firmware_install_error_sub_text)
            .positiveText(R.string.wallet_firmware_install_error_retry_action)
            .onPositive((dialog, which) -> getPresenter().goToPreInstall())
            .negativeText(R.string.wallet_firmware_install_error_cancel_action)
            .onNegative((dialog, which) -> getPresenter().goToDashboard())
            .cancelable(false)
            .show();
   }

   @Override
   public void onStart() {
      installProgress.setVisibility(VISIBLE);
   }
}