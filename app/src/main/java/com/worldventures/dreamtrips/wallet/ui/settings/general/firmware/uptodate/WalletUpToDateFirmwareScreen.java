package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.uptodate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;

public class WalletUpToDateFirmwareScreen
      extends WalletLinearLayout<WalletUpToDateFirmwarePresenter.Screen, WalletUpToDateFirmwarePresenter, WalletUpToDateFirmwarePath>
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

   @NonNull
   @Override
   public WalletUpToDateFirmwarePresenter createPresenter() {
      return new WalletUpToDateFirmwarePresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   public void version(@Nullable SmartCardFirmware version) {
      versionView.setText(version == null ? "" : getString(R.string.wallet_settings_version, version.nordicAppVersion()));
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }
}