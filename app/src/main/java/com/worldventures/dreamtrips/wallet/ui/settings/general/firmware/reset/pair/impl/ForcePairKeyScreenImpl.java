package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.pair.impl;


import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.firmware.command.ConnectForFirmwareUpdate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SmartCardErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.pair.ForcePairKeyPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.pair.ForcePairKeyScreen;
import com.worldventures.dreamtrips.wallet.util.SmartCardConnectException;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class ForcePairKeyScreenImpl extends WalletBaseController<ForcePairKeyScreen, ForcePairKeyPresenter> implements ForcePairKeyScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @Inject ForcePairKeyPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
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
            .positiveText(R.string.ok)
            .show();
   }

   @Override
   public OperationView<ConnectForFirmwareUpdate> provideOperationConnect() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.loading, false),
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

   @OnClick(R.id.button_next)
   public void onConnectToSmartCard() {
      getPresenter().tryToPairAndConnectSmartCard();
   }

   @Override
   public ForcePairKeyPresenter getPresenter() {
      return presenter;
   }
}
