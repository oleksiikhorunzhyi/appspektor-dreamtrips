package com.worldventures.wallet.ui.settings.general.about.impl;

import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.core.utils.AppVersionNameBuilder;
import com.worldventures.wallet.R;
import com.worldventures.wallet.databinding.ScreenWalletAboutBinding;
import com.worldventures.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.settings.general.about.AboutPresenter;
import com.worldventures.wallet.ui.settings.general.about.AboutScreen;
import com.worldventures.wallet.ui.settings.general.about.model.SmartCardAboutViewModel;

import javax.inject.Inject;

import static com.worldventures.wallet.util.SCUserUtils.userFullName;

public class AboutScreenImpl extends WalletBaseController<AboutScreen, AboutPresenter> implements AboutScreen {

   @Inject AboutPresenter presenter;
   @Inject AppVersionNameBuilder appVersionNameBuilder;

   private SmartCardAboutViewModel aboutViewModel = new SmartCardAboutViewModel();

   private ScreenWalletAboutBinding binding;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
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
      aboutViewModel.setAppVersion(appVersionNameBuilder.getReleaseSemanticVersionName());
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

   @Nullable
   @Override
   protected Object screenModule() {
      return new AboutScreenModule();
   }
}
