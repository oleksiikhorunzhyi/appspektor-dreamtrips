package com.worldventures.wallet.ui.wizard.assign.impl;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.wallet.R;
import com.worldventures.wallet.service.command.wizard.WizardCompleteCommand;
import com.worldventures.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.progress.WalletProgressView;
import com.worldventures.wallet.ui.widget.WalletProgressWidget;
import com.worldventures.wallet.ui.wizard.assign.WizardAssignUserPresenter;
import com.worldventures.wallet.ui.wizard.assign.WizardAssignUserScreen;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WizardAssignUserScreenImpl extends WalletBaseController<WizardAssignUserScreen, WizardAssignUserPresenter> implements WizardAssignUserScreen {

   private static final String KEY_PROVISION_MODE = "key_provision_mode";

   private WalletProgressWidget assignProgress;

   @Inject WizardAssignUserPresenter presenter;

   public static WizardAssignUserScreenImpl create(ProvisioningMode provisioningMode) {
      final Bundle args = new Bundle();
      args.putSerializable(KEY_PROVISION_MODE, provisioningMode);
      return new WizardAssignUserScreenImpl(args);
   }

   public WizardAssignUserScreenImpl() {
      super();
   }

   public WizardAssignUserScreenImpl(Bundle args) {
      super(args);
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
      assignProgress = view.findViewById(R.id.assign_progress);
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_assign_smartcard, viewGroup, false);
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
   public OperationView<WizardCompleteCommand> provideOperationView() {
      return new ComposableOperationView<>(new WalletProgressView<>(assignProgress),
            ErrorViewFactory.<WizardCompleteCommand>builder()
                  .addProvider(new HttpErrorViewProvider<>(getContext(), getPresenter().httpErrorHandlingUtil(),
                        command -> getPresenter().onWizardComplete(),
                        command -> getPresenter().onWizardCancel()))
                  .build());
   }

   @Override
   public ProvisioningMode getProvisionMode() {
      return (getArgs() != null && !getArgs().isEmpty() && getArgs().containsKey(KEY_PROVISION_MODE))
            ? (ProvisioningMode) getArgs().getSerializable(KEY_PROVISION_MODE)
            : null;
   }

   @Override
   public WizardAssignUserPresenter getPresenter() {
      return presenter;
   }

   @Nullable
   @Override
   protected Object screenModule() {
      return new WizardAssignUserScreenModule();
   }
}
