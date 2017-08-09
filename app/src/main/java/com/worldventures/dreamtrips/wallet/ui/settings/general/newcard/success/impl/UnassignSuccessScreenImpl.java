package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success.impl;


import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success.UnassignSuccessPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success.UnassignSuccessScreen;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

public class UnassignSuccessScreenImpl extends WalletBaseController<UnassignSuccessScreen, UnassignSuccessPresenter> implements UnassignSuccessScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @Inject UnassignSuccessPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_unassign_success, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   @OnClick(R.id.get_started_button)
   public void onGetStarted() {
      getPresenter().navigateToWizard();
   }

   @Override
   public UnassignSuccessPresenter getPresenter() {
      return presenter;
   }
}
