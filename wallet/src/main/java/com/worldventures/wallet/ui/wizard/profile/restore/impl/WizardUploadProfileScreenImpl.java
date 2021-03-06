package com.worldventures.wallet.ui.wizard.profile.restore.impl;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.wallet.R;
import com.worldventures.wallet.service.command.SetupUserDataCommand;
import com.worldventures.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.common.helper2.progress.WalletProgressView;
import com.worldventures.wallet.ui.widget.WalletProgressWidget;
import com.worldventures.wallet.ui.wizard.profile.restore.WizardUploadProfilePresenter;
import com.worldventures.wallet.ui.wizard.profile.restore.WizardUploadProfileScreen;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WizardUploadProfileScreenImpl extends WalletBaseController<WizardUploadProfileScreen, WizardUploadProfilePresenter> implements WizardUploadProfileScreen {

   private static final String KEY_PROVISION_MODE = "WizardEditProfileScreen#PROVISION_MODE_KEY";

   private WalletProgressWidget progressWidget;

   @Inject WizardUploadProfilePresenter presenter;

   private MaterialDialog retryUploadDataDialog = null;

   public WizardUploadProfileScreenImpl() {
      super();
   }

   public WizardUploadProfileScreenImpl(Bundle args) {
      super(args);
   }

   public static WizardUploadProfileScreenImpl create(@NonNull ProvisioningMode provisioningMode) {
      final Bundle args = new Bundle();
      args.putSerializable(KEY_PROVISION_MODE, provisioningMode);
      return new WizardUploadProfileScreenImpl(args);
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
      progressWidget = view.findViewById(R.id.progress);
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_upload_profile, viewGroup, false);
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
   public OperationView<SetupUserDataCommand> provideOperationSetupUserData() {
      return new ComposableOperationView<>(
            new WalletProgressView<>(progressWidget)
      );
   }

   @Override
   public void showRetryDialog() {
      if (retryUploadDataDialog == null) {
         retryUploadDataDialog = new MaterialDialog.Builder(getContext())
               .content(R.string.wallet_error_uploading_retry)
               .positiveText(R.string.wallet_retry_label)
               .negativeText(R.string.wallet_cancel_label)
               .onPositive((dialog, which) -> getPresenter().retryUpload())
               .build();
      }
      if (!retryUploadDataDialog.isShowing()) {
         retryUploadDataDialog.show();
      }
   }

   @Override
   public ProvisioningMode getProvisionMode() {
      return (!getArgs().isEmpty() && getArgs().containsKey(KEY_PROVISION_MODE))
            ? (ProvisioningMode) getArgs().getSerializable(KEY_PROVISION_MODE)
            : null;
   }

   @Override
   protected void onDetach(@NonNull View view) {
      if (retryUploadDataDialog != null) {
         retryUploadDataDialog.dismiss();
      }
      super.onDetach(view);
   }

   @Override
   public WizardUploadProfilePresenter getPresenter() {
      return presenter;
   }

   @Nullable
   @Override
   protected Object screenModule() {
      return new WizardUploadProfileScreenModule();
   }
}
