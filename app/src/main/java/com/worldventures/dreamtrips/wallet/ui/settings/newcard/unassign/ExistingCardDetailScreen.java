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
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;

public class ExistingCardDetailScreen extends WalletLinearLayout<ExistingCardDetectPresenter.Screen, ExistingCardDetectPresenter, ExistingCardDetectPath> implements ExistingCardDetectPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.tv_sc_id) TextView tvSmartCardId;
   @InjectView(R.id.container_have_card) View containerHaveCard;
   @InjectView(R.id.unassign_button) View unassignButton;

   MaterialDialog confirmUnassignDialog = null;

   public ExistingCardDetailScreen(Context context) {
      super(context);
   }

   public ExistingCardDetailScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public ExistingCardDetectPresenter createPresenter() {
      return new ExistingCardDetectPresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      if (isInEditMode()) return;
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

   @Override
   public void setSmartCardId(String scId) {
      tvSmartCardId.setText(scId);
   }

   @Override
   public void showViewForSCConnected() {
      containerHaveCard.setVisibility(GONE);
      unassignButton.setVisibility(VISIBLE);
   }

   @Override
   public void showViewForSCDisconnected() {
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
   protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      if(confirmUnassignDialog != null) confirmUnassignDialog.dismiss();
   }
}