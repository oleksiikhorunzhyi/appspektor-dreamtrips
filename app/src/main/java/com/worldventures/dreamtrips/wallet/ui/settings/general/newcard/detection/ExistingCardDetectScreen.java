package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.detection;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class ExistingCardDetectScreen extends WalletLinearLayout<ExistingCardDetectPresenter.Screen, ExistingCardDetectPresenter, ExistingCardDetectPath> implements ExistingCardDetectPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.tv_sc_id) TextView tvSmartCardId;
   @InjectView(R.id.unassign_button) Button unassignButton;
   @InjectView(R.id.have_card_button) View haveCardButton;

   public ExistingCardDetectScreen(Context context) {
      super(context);
   }

   public ExistingCardDetectScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public ExistingCardDetectPresenter createPresenter() {
      return new ExistingCardDetectPresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      supportConnectionStatusLabel(false);
      super.onFinishInflate();
      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @OnClick(R.id.have_card_button)
   public void onClickHaveCard() {
      presenter.navigateToPowerOn();
   }

   @Override
   public OperationView<ActiveSmartCardCommand> provideOperationView() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.loading, true),
            ErrorViewFactory.<ActiveSmartCardCommand>builder().build()
      );
   }

   @Override
   public OperationView<DeviceStateCommand> provideDeviceStateOperationView() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.loading, true),
            ErrorViewFactory.<DeviceStateCommand>builder().build()
      );
   }

   @Override
   public OperationView<WipeSmartCardDataCommand> provideWipeOperationView() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.loading, true),
            ErrorViewFactory.<WipeSmartCardDataCommand>builder().build()
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
      presenter.prepareUnassignCardOnBackend();
   }

   private void unassignCardClick() {
      presenter.prepareUnassignCard();
   }

   @Override
   public void showConfirmationUnassignDialog(String scId) {
      new MaterialDialog.Builder(getContext())
            .content(ProjectTextUtils.fromHtml(getString(R.string.wallet_unassign_card_confirm_message, scId)))
            .positiveText(R.string.wallet_continue_label)
            .onPositive((dialog, which) -> presenter.unassignCard())
            .negativeText(R.string.cancel)
            .show();
   }

   @Override
   public void showConfirmationUnassignOnBackend(String scId) {
      new MaterialDialog.Builder(getContext())
            .content(ProjectTextUtils.fromHtml(getString(R.string.wallet_settings_dont_have_card_msg, scId)))
            .positiveText(R.string.wallet_continue_label)
            .negativeText(R.string.cancel)
            .onPositive((dialog, which) -> presenter.unassignCardOnBackend())
            .show();
   }
}