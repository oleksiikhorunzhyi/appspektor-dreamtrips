package com.worldventures.dreamtrips.wallet.ui.settings.firmware.uptodate;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;

public class WalletUpToDateFirmwareScreen
      extends WalletFrameLayout<WalletUpToDateFirmwarePresenter.Screen, WalletUpToDateFirmwarePresenter, WalletUpToDateFirmwarePath>
      implements WalletUpToDateFirmwarePresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.version) TextView versionView;

   public WalletUpToDateFirmwareScreen(Context context) {
      super(context);
   }

   public WalletUpToDateFirmwareScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
   }

   @Override
   public WalletUpToDateFirmwarePresenter createPresenter() {
      return new WalletUpToDateFirmwarePresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   public void version(String version) {
      versionView.setText(getResources().getString(R.string.wallet_settings_version, version));
   }
}