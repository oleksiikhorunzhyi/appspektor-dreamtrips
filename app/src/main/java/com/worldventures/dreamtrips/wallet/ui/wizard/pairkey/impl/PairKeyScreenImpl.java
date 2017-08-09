package com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.impl;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.PairKeyPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.PairKeyScreen;
import com.worldventures.dreamtrips.wallet.util.SmartCardConnectException;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class PairKeyScreenImpl extends WalletBaseController<PairKeyScreen, PairKeyPresenter> implements PairKeyScreen {

   private static final String KEY_PROVISION_MODE = "key_provision_mode";
   private static final String KEY_BARCODE = "key_barcode";

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @Inject PairKeyPresenter presenter;

   public static PairKeyScreenImpl create(ProvisioningMode mode, String barcode) {
      final Bundle args = new Bundle();
      args.putSerializable(KEY_PROVISION_MODE, mode);
      args.putString(KEY_BARCODE, barcode);
      return new PairKeyScreenImpl(args);
   }

   public PairKeyScreenImpl() {
      super();
   }

   public PairKeyScreenImpl(Bundle args) {
      super(args);
   }

   @OnClick(R.id.button_next)
   public void onConnectToSmartCard() {
      getPresenter().tryToPairAndConnectSmartCard();
   }

   @Override
   public OperationView<CreateAndConnectToCardCommand> provideOperationCreateAndConnect() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.loading, false),
            ErrorViewFactory.<CreateAndConnectToCardCommand>builder()
                  .addProvider(new SimpleDialogErrorViewProvider<>(
                        getContext(),
                        SmartCardConnectException.class,
                        R.string.wallet_smartcard_connection_error,
                        command -> getPresenter().goBack())
                  ).build());
   }

   @Override
   public ProvisioningMode getProvisionMode() {
      return (getArgs() != null && !getArgs().isEmpty() && getArgs().containsKey(KEY_PROVISION_MODE))
            ? (ProvisioningMode) getArgs().getSerializable(KEY_PROVISION_MODE)
            : null;
   }

   @Override
   public String getBarcode() {
      return (getArgs() != null && !getArgs().isEmpty() && getArgs().containsKey(KEY_BARCODE))
            ? getArgs().getString(KEY_BARCODE)
            : null;
   }

   @Override
   public void showBackButton() {
      toolbar.setNavigationIcon(R.drawable.ic_wallet_vector_arrow_back);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
   }

   @Override
   public void hideBackButton() {
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
      toolbar.setNavigationOnClickListener(null);
   }

   @Override
   public PairKeyPresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_pairkey, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }
}
