package com.worldventures.wallet.ui.settings.general.about.impl;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
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

import timber.log.Timber;

import static com.worldventures.wallet.util.SCUserUtils.userFullName;

public class AboutScreenImpl extends WalletBaseController<AboutScreen, AboutPresenter> implements AboutScreen {

   private static final String KEY_STATE_ABOUT_VIEW_MODEL = "AboutScreenImpl#KEY_STATE_CONTENT_LOADED";

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
      return binding.getRoot();
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      binding.setAboutViewModel(aboutViewModel);
      if (aboutViewModel.isEmpty()) {
         presenter.fetchAboutInfo();
      }
   }

   @Override
   protected void onSaveViewState(@NonNull View view, @NonNull Bundle outState) {
      super.onSaveViewState(view, outState);
      outState.putParcelable(KEY_STATE_ABOUT_VIEW_MODEL, aboutViewModel);
   }

   @Override
   protected void onRestoreViewState(@NonNull View view, @NonNull Bundle savedViewState) {
      super.onRestoreViewState(view, savedViewState);
      aboutViewModel = savedViewState.getParcelable(KEY_STATE_ABOUT_VIEW_MODEL);
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
      if (smartCardUser == null) {
         String message = String.format("User is null in SmartCardUserCommand storage in %s screen",
               getClass().getSimpleName());
         Timber.e(message);
         Crashlytics.log(message);
         return;
      }
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
