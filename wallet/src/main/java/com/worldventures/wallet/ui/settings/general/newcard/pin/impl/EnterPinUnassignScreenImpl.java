package com.worldventures.wallet.ui.settings.general.newcard.pin.impl;

import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.wallet.R;
import com.worldventures.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.settings.general.newcard.pin.EnterPinUnassignPresenter;
import com.worldventures.wallet.ui.settings.general.newcard.pin.EnterPinUnassignScreen;
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetDelegate;
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetOperationView;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.OperationView;

public class EnterPinUnassignScreenImpl extends WalletBaseController<EnterPinUnassignScreen, EnterPinUnassignPresenter> implements EnterPinUnassignScreen {

   @Inject EnterPinUnassignPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_enter_pin_for_new_card, viewGroup, false);
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
   public OperationView<ResetSmartCardCommand> provideResetOperationView(FactoryResetDelegate factoryResetDelegate) {
      return FactoryResetOperationView.create(getContext(),
            factoryResetDelegate::factoryReset,
            factoryResetDelegate::goBack,
            R.string.wallet_error_enter_pin_title,
            R.string.wallet_error_enter_pin_msg,
            R.string.wallet_retry_label,
            R.string.wallet_cancel_label,
            R.string.wallet_loading,
            true);
   }

   @Override
   public EnterPinUnassignPresenter getPresenter() {
      return presenter;
   }

   @Nullable
   @Override
   protected Object screenModule() {
      return new EnterPinUnassignScreenModule();
   }
}
