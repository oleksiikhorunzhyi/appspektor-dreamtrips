package com.worldventures.wallet.ui.settings.general.firmware.reset.pair.impl;


import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.wallet.R;
import com.worldventures.wallet.service.firmware.command.ConnectForFirmwareUpdate;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.error.SmartCardErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.wallet.ui.settings.general.firmware.reset.pair.ForcePairKeyPresenter;
import com.worldventures.wallet.ui.settings.general.firmware.reset.pair.ForcePairKeyScreen;
import com.worldventures.wallet.util.SmartCardConnectException;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class ForcePairKeyScreenImpl extends WalletBaseController<ForcePairKeyScreen, ForcePairKeyPresenter> implements ForcePairKeyScreen {

   @Inject ForcePairKeyPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      final Button btnNext = view.findViewById(R.id.button_next);
      btnNext.setOnClickListener(btn -> getPresenter().tryToPairAndConnectSmartCard());
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_force_fw_update_pairkey, viewGroup, false);
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
   public void showError(@StringRes int messageId) {
      new MaterialDialog.Builder(getContext())
            .content(messageId)
            .positiveText(R.string.wallet_ok)
            .show();
   }

   @Override
   public OperationView<ConnectForFirmwareUpdate> provideOperationConnect() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_loading, false),
            ErrorViewFactory.<ConnectForFirmwareUpdate>builder()
                  .addProvider(new SimpleDialogErrorViewProvider<>(
                        getContext(),
                        SmartCardConnectException.class,
                        R.string.wallet_smartcard_connection_error,
                        command -> getPresenter().goBack()))
                  .addProvider(new SmartCardErrorViewProvider<>(getContext()))
                  .build()
      );
   }

   @Override
   public ForcePairKeyPresenter getPresenter() {
      return presenter;
   }
}
