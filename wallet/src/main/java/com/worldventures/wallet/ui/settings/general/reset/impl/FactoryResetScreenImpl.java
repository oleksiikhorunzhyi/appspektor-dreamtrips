package com.worldventures.wallet.ui.settings.general.reset.impl;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldventures.wallet.R;
import com.worldventures.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetDelegate;
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetOperationView;
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetPresenter;
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetScreen;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.OperationView;

public class FactoryResetScreenImpl extends WalletBaseController<FactoryResetScreen, FactoryResetPresenter> implements FactoryResetScreen {

   @Inject FactoryResetPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      final TextView btnCancel = view.findViewById(R.id.btn_cancel);
      btnCancel.setOnClickListener(cancelBtn -> getPresenter().goBack());
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
   public FactoryResetPresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_factory_reset, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   @Nullable
   @Override
   protected Object screenModule() {
      return new FactoryResetScreenModule();
   }
}
