package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success.impl;


import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success.UnassignSuccessPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success.UnassignSuccessScreen;

import javax.inject.Inject;

public class UnassignSuccessScreenImpl extends WalletBaseController<UnassignSuccessScreen, UnassignSuccessPresenter> implements UnassignSuccessScreen {

   @Inject UnassignSuccessPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      final Button btnGetStarted = view.findViewById(R.id.get_started_button);
      btnGetStarted.setOnClickListener(getStartedBtn -> getPresenter().navigateToWizard());
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

   @Override
   public UnassignSuccessPresenter getPresenter() {
      return presenter;
   }
}
