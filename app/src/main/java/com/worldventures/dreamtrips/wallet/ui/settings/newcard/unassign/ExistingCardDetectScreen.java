package com.worldventures.dreamtrips.wallet.ui.settings.newcard.unassign;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.settings.newcard.helper.ProgressDialogView;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class ExistingCardDetectScreen extends WalletLinearLayout<ExistingCardDetectPresenter.Screen, ExistingCardDetectPresenter, ExistingCardDetectPath> implements ExistingCardDetectPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.tv_sc_id) TextView tvSmartCardId;
   @InjectView(R.id.container_have_card) View containerHaveCard;
   @InjectView(R.id.unassign_button) View unassignButton;

   MaterialDialog confirmUnassignDialog = null;
   MaterialDialog dontHaveCardDialog = null;

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

   @OnClick(R.id.unassign_button)
   public void onUnassignCard() {
      presenter.prepareUnassignCard();
   }

   @OnClick(R.id.dont_have_card_button)
   public void onClickDontHaveCard() {
      presenter.prepareUnassignCardOnBackend();
   }

   @OnClick(R.id.have_card_button)
   public void onClickHaveCard() {
      presenter.navigateToPowerOn();
   }

   @Override
   public OperationView<ActiveSmartCardCommand> provideOperationView() {
      return new ComposableOperationView<>(
            ProgressDialogView.<ActiveSmartCardCommand>builder(getContext()).build(),
            ErrorViewFactory.<ActiveSmartCardCommand>builder().build()
      );
   }

   @Override
   public OperationView<WipeSmartCardDataCommand> provideWipeOperationView() {
      return new ComposableOperationView<>(
            ProgressDialogView.<WipeSmartCardDataCommand>builder(getContext()).build(),
            ErrorViewFactory.<WipeSmartCardDataCommand>builder().build()
      );
   }

   @Override
   public void setSmartCardId(String scId) {
      tvSmartCardId.setText(scId);
   }

   @Override
   public void modeConnectedSmartCard() {
      containerHaveCard.setVisibility(GONE);
      unassignButton.setVisibility(VISIBLE);
   }

   @Override
   public void modeDisconnectedSmartCard() {
      containerHaveCard.setVisibility(VISIBLE);
      unassignButton.setVisibility(GONE);
   }

   @Override
   public void showConfirmationUnassignDialog(String scId) {
      if (confirmUnassignDialog == null) {
         confirmUnassignDialog = new MaterialDialog.Builder(getContext())
               .content(Html.fromHtml(getString(R.string.wallet_unassign_card_confirm_message, scId)))
               .positiveText(R.string.wallet_continue_label)
               .onPositive((dialog, which) -> presenter.unassignCard())
               .negativeText(R.string.cancel)
               .build();
      }
      if(!confirmUnassignDialog.isShowing()) confirmUnassignDialog.show();
   }

   @Override
   public void showConfirmationUnassignOnBackend(String scId) {
      if (dontHaveCardDialog == null) {
         dontHaveCardDialog = new MaterialDialog.Builder(getContext())
               .content(Html.fromHtml(getString(R.string.wallet_settings_dont_have_card_msg, scId)))
               .positiveText(R.string.wallet_continue_label)
               .negativeText(R.string.cancel)
               .onPositive((dialog, which) -> presenter.unassignCardOnBackend())
               .build();
      }

      if(!dontHaveCardDialog.isShowing()) dontHaveCardDialog.show();
   }

   @Override
   protected void onDetachedFromWindow() {
      if(confirmUnassignDialog != null) confirmUnassignDialog.dismiss();
      if(dontHaveCardDialog != null) dontHaveCardDialog.dismiss();
      super.onDetachedFromWindow();
   }
}