package com.worldventures.dreamtrips.wallet.ui.settings.about;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;

public class AboutScreen extends WalletLinearLayout<AboutPresenter.Screen, AboutPresenter, AboutPath> implements AboutPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.tvUserName) TextView tvUserName;
   @InjectView(R.id.tvSmartCardId) TextView tvSmartCardId;
   @InjectView(R.id.tvQtyCardStored) TextView tvQtyCardStored;
   @InjectView(R.id.tvQtyCardAvailable) TextView tvQtyCardAvailable;
   @InjectView(R.id.tvDTAppVersion) TextView tvDTAppVersion;
   @InjectView(R.id.tvNordicFWVersion) TextView tvNordicFWVersion;
   @InjectView(R.id.tvAtmelCardFWVersion) TextView tvAtmelCardFWVersion;
   @InjectView(R.id.tvBootLoaderFWVersion) TextView tvBootLoaderFWVersion;
   @InjectView(R.id.tvAtmelChargerFWVersion) TextView tvAtmelChargerFWVersion;

   private DialogOperationScreen dialogOperationScreen;

   public AboutScreen(Context context) {
      super(context);
   }

   public AboutScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public AboutPresenter createPresenter() {
      return new AboutPresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      provideAppVersion();
   }

   private void provideAppVersion() {
      tvDTAppVersion.setText(BuildConfig.VERSION_NAME);
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return dialogOperationScreen == null ? dialogOperationScreen = new DialogOperationScreen(this) : dialogOperationScreen;
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @Override
   public void onProvideSmartCard(final SmartCardFirmware smartCardFirmware, final String smartCardId, final SmartCardUser user) {
      tvUserName.setText(user.fullName());
      tvSmartCardId.setText(smartCardId);
      tvNordicFWVersion.setText(smartCardFirmware.firmwareVersion());
      tvAtmelCardFWVersion.setText(smartCardFirmware.internalAtmelVersion());
      tvBootLoaderFWVersion.setText(smartCardFirmware.nrfBootloaderVersion());
      tvAtmelChargerFWVersion.setText(smartCardFirmware.externalAtmelVersion());
   }

   @Override
   public void onProvidePayCardInfo(final int cardStored, final int cardAvailable) {
      tvQtyCardStored.setText(String.valueOf(cardStored));
      tvQtyCardAvailable.setText(String.valueOf(cardAvailable));
   }
}