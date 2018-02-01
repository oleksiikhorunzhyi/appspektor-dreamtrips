package com.worldventures.wallet.ui.settings.general.newcard.detection.impl;

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
import com.worldventures.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.wallet.ui.common.helper2.error.SmartCardErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.wallet.ui.settings.general.newcard.detection.ExistingCardDetectPresenter;
import com.worldventures.wallet.ui.settings.general.newcard.detection.ExistingCardDetectScreen;
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetDelegate;
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetOperationView;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ExistingCardDetectScreenImpl extends WalletBaseController<ExistingCardDetectScreen, ExistingCardDetectPresenter> implements ExistingCardDetectScreen {

   private TextView tvSmartCardId;
   private Button unassignButton;
   private TextView haveCardButton;

   @Inject ExistingCardDetectPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      tvSmartCardId = view.findViewById(R.id.tv_sc_id);
      unassignButton = view.findViewById(R.id.unassign_button);
      haveCardButton = view.findViewById(R.id.have_card_button);
      haveCardButton.setOnClickListener(btnHaveCard -> getPresenter().navigateToPowerOn());
   }

   @Override
   public OperationView<ActiveSmartCardCommand> provideActiveSmartCardOperationView() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_loading, true),
            ErrorViewFactory.<ActiveSmartCardCommand>builder()
                  .addProvider(new SmartCardErrorViewProvider<>(getContext()))
                  .build()
      );
   }

   @Override
   public OperationView<DeviceStateCommand> provideDeviceStateOperationView() {
      return new ComposableOperationView<>(new SimpleDialogProgressView<>(getContext(), R.string.wallet_loading, true));
   }

   @Override
   public OperationView<WipeSmartCardDataCommand> provideWipeOperationView() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_loading, true),
            ErrorViewFactory.<WipeSmartCardDataCommand>builder()
                  .addProvider(new SmartCardErrorViewProvider<>(getContext()))
                  .addProvider(new HttpErrorViewProvider<>(getContext(), getPresenter().httpErrorHandlingUtil(),
                        command -> unassignButton.performClick(), command -> { /*nothing*/ }))
                  .build()
      );
   }

   @Override
   public void setSmartCardId(String scId) {
      tvSmartCardId.setText(scId);
   }

   @Override
   public void modeConnectedSmartCard() {
      unassignButton.setText(R.string.wallet_unassign_card);
      unassignButton.setOnClickListener(v -> unassignCardClick());
      haveCardButton.setVisibility(GONE);
   }

   @Override
   public void modeDisconnectedSmartCard() {
      unassignButton.setText(R.string.wallet_i_dont_have_card);
      unassignButton.setOnClickListener(v -> doNotHaveCardClick());
      haveCardButton.setVisibility(VISIBLE);
   }

   private void doNotHaveCardClick() {
      getPresenter().prepareUnassignCardOnBackend();
   }

   private void unassignCardClick() {
      getPresenter().prepareUnassignCard();
   }

   @Override
   public void showConfirmationUnassignDialog(String scId) {
      new MaterialDialog.Builder(getContext())
            .content(ProjectTextUtils.fromHtml(getString(R.string.wallet_unassign_card_confirm_message, scId)))
            .positiveText(R.string.wallet_continue_label)
            .onPositive((dialog, which) -> getPresenter().unassignCard())
            .negativeText(R.string.wallet_cancel_label)
            .show();
   }

   @Override
   public void showConfirmationUnassignOnBackend(String scId) {
      new MaterialDialog.Builder(getContext())
            .content(ProjectTextUtils.fromHtml(getString(R.string.wallet_settings_dont_have_card_msg, scId)))
            .positiveText(R.string.wallet_continue_label)
            .negativeText(R.string.wallet_cancel_label)
            .onPositive((dialog, which) -> getPresenter().unassignCardOnBackend())
            .show();
   }

   @Override
   public OperationView<ResetSmartCardCommand> provideResetOperationView(FactoryResetDelegate factoryResetDelegate) {
      return FactoryResetOperationView.create(getContext(),
            factoryResetDelegate::factoryReset,
            () -> {
            },
            R.string.wallet_error_enter_pin_title,
            R.string.wallet_error_enter_pin_msg,
            R.string.wallet_retry_label,
            R.string.wallet_cancel_label,
            R.string.wallet_loading,
            false);
   }

   @Override
   public ExistingCardDetectPresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_existing_card_detect, viewGroup, false);
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
      return new ExistingCardDetectScreenModule();
   }
}
