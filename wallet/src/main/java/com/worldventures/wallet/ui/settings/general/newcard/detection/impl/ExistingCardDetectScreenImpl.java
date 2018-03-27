package com.worldventures.wallet.ui.settings.general.newcard.detection.impl;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.worldventures.wallet.domain.entity.ConnectionStatus;
import com.worldventures.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.settings.general.newcard.detection.ExistingCardDetectPresenter;
import com.worldventures.wallet.ui.settings.general.newcard.detection.ExistingCardDetectScreen;
import com.worldventures.wallet.ui.settings.general.newcard.helper.CardIdUtil;
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetDelegate;
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetOperationView;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.OperationView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ExistingCardDetectScreenImpl extends WalletBaseController<ExistingCardDetectScreen, ExistingCardDetectPresenter> implements ExistingCardDetectScreen {

   private static final String KEY_STATUS_SMART_CARD_ID = "ExistingCardDetectScreenImpl#KEY_STATUS_SMART_CARD_ID";
   private static final String KEY_STATUS_SMART_CARD_CONNECTED = "ExistingCardDetectScreenImpl#KEY_STATUS_SMART_CARD_CONNECTED";

   private TextView tvSmartCardId;
   private Button unassignButton;
   private TextView haveCardButton;

   private String smartCardId;
   private boolean smartCardConnected;

   private OperationView<ResetSmartCardCommand> resetOperationView;

   @Inject ExistingCardDetectPresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      tvSmartCardId = view.findViewById(R.id.tv_sc_id);
      unassignButton = view.findViewById(R.id.unassign_button);
      haveCardButton = view.findViewById(R.id.have_card_button);
      haveCardButton.setOnClickListener(btnHaveCard -> getPresenter().cardAvailable());
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      if (smartCardId == null) {
         presenter.fetchSmartCardId();
         presenter.fetchSmartCardConnection();
      }
   }

   @Override
   public void setSmartCardConnection(ConnectionStatus connection) {
      bindConnectionStatus(connection.isConnected());
   }

   private void bindConnectionStatus(boolean connected) {
      this.smartCardConnected = connected;
      if (connected) {
         modeConnectedSmartCard();
      } else {
         modeDisconnectedSmartCard();
      }
   }

   private void modeConnectedSmartCard() {
      unassignButton.setText(R.string.wallet_unassign_card);
      unassignButton.setOnClickListener(v -> unassignCardClick());
      haveCardButton.setVisibility(GONE);
   }

   private void modeDisconnectedSmartCard() {
      unassignButton.setText(R.string.wallet_i_dont_have_card);
      unassignButton.setOnClickListener(v -> doNotHaveCardClick());
      haveCardButton.setVisibility(VISIBLE);
   }

   @Override
   public void setSmartCardId(String scId) {
      smartCardId = scId;
      tvSmartCardId.setText(CardIdUtil.pushZeroToSmartCardId(scId));
   }

   private void doNotHaveCardClick() {
      getPresenter().unassignWithoutCard();
   }

   private void unassignCardClick() {
      getPresenter().unassignCard();
   }

   @Override
   public void showConfirmationUnassignDialog() {
      new MaterialDialog.Builder(getContext())
            .content(ProjectTextUtils.fromHtml(getString(R.string.wallet_unassign_card_confirm_message, smartCardId)))
            .positiveText(R.string.wallet_continue_label)
            .onPositive((dialog, which) -> getPresenter().unassignCardConfirmed(smartCardId))
            .negativeText(R.string.wallet_cancel_label)
            .show();
   }

   @Override
   public void showConfirmationUnassignWhioutCard() {
      new MaterialDialog.Builder(getContext())
            .content(ProjectTextUtils.fromHtml(getString(R.string.wallet_settings_dont_have_card_msg, smartCardId)))
            .positiveText(R.string.wallet_continue_label)
            .negativeText(R.string.wallet_cancel_label)
            .onPositive((dialog, which) -> getPresenter().unassignWithoutCardConfirmed(smartCardId))
            .show();
   }

   @Override
   public OperationView<ResetSmartCardCommand> provideResetOperationView(FactoryResetDelegate factoryResetDelegate) {
      if (resetOperationView == null) {
         resetOperationView = FactoryResetOperationView.create(getContext(),
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
      return resetOperationView;
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

   @Override
   protected void onSaveViewState(@NonNull View view, @NonNull Bundle outState) {
      super.onSaveViewState(view, outState);
      outState.putString(KEY_STATUS_SMART_CARD_ID, smartCardId);
      outState.putBoolean(KEY_STATUS_SMART_CARD_CONNECTED, smartCardConnected);
   }

   @Override
   protected void onRestoreViewState(@NonNull View view, @NonNull Bundle savedViewState) {
      super.onRestoreViewState(view, savedViewState);
      setSmartCardId(savedViewState.getString(KEY_STATUS_SMART_CARD_ID));
      bindConnectionStatus(savedViewState.getBoolean(KEY_STATUS_SMART_CARD_CONNECTED));
   }

   @Nullable
   @Override
   protected Object screenModule() {
      return new ExistingCardDetectScreenModule();
   }
}
