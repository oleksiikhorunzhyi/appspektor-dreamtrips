package com.worldventures.dreamtrips.wallet.ui.settings.general.about.impl;


import android.databinding.DataBindingUtil;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.databinding.ScreenWalletAboutBinding;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.settings.general.about.AboutPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.about.AboutScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.about.model.SmartCardAboutViewModel;

import javax.inject.Inject;

import butterknife.InjectView;

import static com.worldventures.dreamtrips.wallet.util.SCUserUtils.userFullName;

public class AboutScreenImpl extends WalletBaseController<AboutScreen, AboutPresenter> implements AboutScreen {

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

   @Inject AboutPresenter presenter;

   private SmartCardAboutViewModel aboutViewModel = new SmartCardAboutViewModel();

   private ScreenWalletAboutBinding binding;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      provideAppVersion();
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      binding = DataBindingUtil.inflate(layoutInflater, R.layout.screen_wallet_about, viewGroup, false);
      binding.setAboutViewModel(aboutViewModel);
      return binding.getRoot();
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   private void provideAppVersion() {
      aboutViewModel.setAppVersion(BuildConfig.VERSION_NAME);
   }

   @Override
   public void onProvidePayCardInfo(final int cardStored, final int cardAvailable) {
      aboutViewModel.setCardsStored(String.valueOf(cardStored));
      aboutViewModel.setCardsAvailable(String.valueOf(cardAvailable));
   }

   @Override
   public void setSmartCardId(String smartCardId) {
      aboutViewModel.setSmartCardId(smartCardId);
   }

   @Override
   public void setSmartCardFirmware(SmartCardFirmware smartCardFirmware) {
      aboutViewModel.setSmartCardFirmware(smartCardFirmware);
   }

   @Override
   public void setSmartCardUser(SmartCardUser smartCardUser) {
      aboutViewModel.setSmartCardUserFullName(userFullName(smartCardUser));
   }

   @Override
   public AboutPresenter getPresenter() {
      return presenter;
   }
}
