package com.worldventures.dreamtrips.wallet.ui.settings.firmware.donwload;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.functions.Action1;

public class WalletDownloadFirmwareScreen extends WalletFrameLayout<WalletDownloadFirmwarePresenter.Screen, WalletDownloadFirmwarePresenter, WalletDownloadFirmwarePath>
      implements WalletDownloadFirmwarePresenter.Screen, OperationScreen<Void> {

   @InjectView(R.id.firmware_download_progress) View downloadProgress;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   public WalletDownloadFirmwareScreen(Context context) {
      super(context);
   }

   public WalletDownloadFirmwareScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public WalletDownloadFirmwarePresenter createPresenter() {
      return new WalletDownloadFirmwarePresenter(getContext(), getInjector(), getPath().firmwareInfo(), getPath().filePath());
   }


   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      toolbar.setNavigationIcon(null);
   }

   @OnClick(R.id.wallet_cancel_download)
   void cancelDownload() {
      presenter.cancelDownload();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return this;
   }

   @Override
   public void informSuccess() {
      Snackbar.make(this, "Download success, in feature you will see next screen", Snackbar.LENGTH_SHORT).show();
   }

   @Override
   public void showProgress() {
      Animation a = AnimationUtils.loadAnimation(getContext(), R.anim.wallet_progress_anim);
      a.setDuration(1000);
      downloadProgress.startAnimation(a);
   }

   @Override
   public void hideProgress() {
      //needless
   }

   @Override
   public void showError(String msg, @Nullable Action1<Void> action) {
      Snackbar.make(this, msg, Snackbar.LENGTH_SHORT)
            .setCallback(new Snackbar.Callback() {
               @Override
               public void onDismissed(Snackbar snackbar, int event) {
                  if (action != null) action.call(null);
               }
            }).show();
   }

   @Override
   public Context context() {
      return getContext();
   }
}
