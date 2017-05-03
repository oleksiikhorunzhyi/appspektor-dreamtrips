package com.worldventures.dreamtrips.wallet.ui.settings.firmware.download;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletProgressWidget;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.functions.Action1;

public class WalletDownloadFirmwareScreen extends WalletLinearLayout<WalletDownloadFirmwarePresenter.Screen, WalletDownloadFirmwarePresenter, WalletDownloadFirmwarePath>
      implements WalletDownloadFirmwarePresenter.Screen, OperationScreen<Void> {

   @InjectView(R.id.firmware_download_progress) WalletProgressWidget downloadProgress;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   public WalletDownloadFirmwareScreen(Context context) {
      super(context);
   }

   public WalletDownloadFirmwareScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public WalletDownloadFirmwarePresenter createPresenter() {
      return new WalletDownloadFirmwarePresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      supportConnectionStatusLabel(false);
      super.onFinishInflate();
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      //set color transparent for add space without white back arrow
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
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
   public void showProgress(@Nullable String text) {
      downloadProgress.start();
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

   @Override
   protected boolean hasToolbar() {
      return true;
   }
}
