package com.worldventures.dreamtrips.wallet.ui.wizard.unassign.impl;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.wallet.service.command.wizard.ReAssignCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.helper.CardIdUtil;
import com.worldventures.dreamtrips.wallet.ui.wizard.unassign.ExistingDeviceDetectPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.unassign.ExistingDeviceDetectScreen;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class ExistingDeviceDetectScreenImpl extends WalletBaseController<ExistingDeviceDetectScreen, ExistingDeviceDetectPresenter> implements ExistingDeviceDetectScreen {

   private static final String KEY_SMARTCARD_ID = "key_smartcard_id";

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.tv_sc_id) TextView tvSmartCardId;

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
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
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
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @OnClick(R.id.button_unpair)
   public void onClickUnpair() {
      getPresenter().repair();
   }

   @Override
   public OperationView<ReAssignCardCommand> provideOperationView() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_existing_device_detect_progress, false),
            ErrorViewFactory.<ReAssignCardCommand>builder()
                  .addProvider(new HttpErrorViewProvider<>(getContext(), getPresenter().httpErrorHandlingUtil(), command -> getPresenter().retryReAssigning(), command -> { /*nothing*/}))
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
}
