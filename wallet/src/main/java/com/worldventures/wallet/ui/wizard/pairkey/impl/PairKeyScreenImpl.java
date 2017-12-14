package com.worldventures.wallet.ui.wizard.pairkey.impl;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.worldventures.wallet.R;
import com.worldventures.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.wallet.ui.wizard.pairkey.PairKeyPresenter;
import com.worldventures.wallet.ui.wizard.pairkey.PairKeyScreen;
import com.worldventures.wallet.util.SmartCardConnectException;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class PairKeyScreenImpl extends WalletBaseController<PairKeyScreen, PairKeyPresenter> implements PairKeyScreen {

   private static final String KEY_PROVISION_MODE = "key_provision_mode";
   private static final String KEY_BARCODE = "key_barcode";

   private Toolbar toolbar;
   private Button btnNext;

   @Inject PairKeyPresenter presenter;

   private OperationView<CreateAndConnectToCardCommand> operationView;

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

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar = view.findViewById(R.id.toolbar);
      btnNext = view.findViewById(R.id.button_next);
      btnNext.setOnClickListener(next -> getPresenter().tryToPairAndConnectSmartCard());

      operationView = new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_loading, false),
            ErrorViewFactory.<CreateAndConnectToCardCommand>builder()
                  .addProvider(new SimpleDialogErrorViewProvider<>(
                        getContext(),
                        SmartCardConnectException.class,
                        R.string.wallet_smartcard_connection_error,
                        command -> getPresenter().goBack())
                  ).build());
   }

   @Override
   public OperationView<CreateAndConnectToCardCommand> provideOperationCreateAndConnect() {
      return operationView;
   }

   @Override
   public ProvisioningMode getProvisionMode() {
      return getArgs().containsKey(KEY_PROVISION_MODE) ? (ProvisioningMode) getArgs().getSerializable(KEY_PROVISION_MODE) : null;
   }

   @Override
   public String getBarcode() {
      return getArgs().containsKey(KEY_BARCODE) ? getArgs().getString(KEY_BARCODE) : null;
   }

   @Override
   protected void onDetach(@NonNull View view) {
      super.onDetach(view);
      operationView.hideError();
      operationView.hideProgress();
   }

   @Override
   public void nextButtonEnable(boolean enable) {
      btnNext.setEnabled(enable);
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
