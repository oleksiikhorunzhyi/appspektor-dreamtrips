package com.worldventures.wallet.ui.wizard.unassign.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.core.utils.ProjectTextUtils;
import com.worldventures.wallet.R;
import com.worldventures.wallet.service.command.wizard.ReAssignCardCommand;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.wallet.ui.settings.general.newcard.helper.CardIdUtil;
import com.worldventures.wallet.ui.wizard.unassign.ExistingDeviceDetectPresenter;
import com.worldventures.wallet.ui.wizard.unassign.ExistingDeviceDetectScreen;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class ExistingDeviceDetectScreenImpl extends WalletBaseController<ExistingDeviceDetectScreen, ExistingDeviceDetectPresenter> implements ExistingDeviceDetectScreen {

   private static final String KEY_SMARTCARD_ID = "key_smartcard_id";

   private TextView tvSmartCardId;

   @Inject ExistingDeviceDetectPresenter presenter;

   public static ExistingDeviceDetectScreenImpl create(String smartCardId) {
      final Bundle args = new Bundle();
      args.putString(KEY_SMARTCARD_ID, smartCardId);
      return new ExistingDeviceDetectScreenImpl(args);

   }

   public ExistingDeviceDetectScreenImpl() {
      super();
   }

   public ExistingDeviceDetectScreenImpl(Bundle args) {
      super(args);
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      final Button btnUnpair = view.findViewById(R.id.button_unpair);
      btnUnpair.setOnClickListener(unpair -> getPresenter().repair());
      tvSmartCardId = view.findViewById(R.id.tv_sc_id);
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_existing_device_detect, viewGroup, false);
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
   public OperationView<ReAssignCardCommand> provideOperationView() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_existing_device_detect_progress, false),
            ErrorViewFactory.<ReAssignCardCommand>builder()
                  .addProvider(new HttpErrorViewProvider<>(getContext(), getPresenter().httpErrorHandlingUtil(), command -> getPresenter()
                        .retryReAssigning(), command -> { /*nothing*/ }))
                  .build()
      );
   }

   @Override
   public void setSmartCardId(String scId) {
      tvSmartCardId.setText(CardIdUtil.pushZeroToSmartCardId(scId));
   }

   @Override
   public void showConfirmDialog(String scId) {
      new MaterialDialog.Builder(getContext())
            .content(ProjectTextUtils.fromHtml(getString(R.string.wallet_existing_device_detect_unpair_dialog, scId)))
            .positiveText(R.string.wallet_continue_label)
            .onPositive((dialog, which) -> getPresenter().repairConfirmed())
            .negativeText(R.string.wallet_cancel_label)
            .show();
   }

   @Override
   public String getSmartCardId() {
      return getArgs().getString(KEY_SMARTCARD_ID);
   }

   @Override
   public ExistingDeviceDetectPresenter getPresenter() {
      return presenter;
   }

   @Nullable
   @Override
   protected Object screenModule() {
      return new ExistingDeviceDetectScreenModule();
   }
}
