package com.worldventures.dreamtrips.wallet.ui.settings.newcard.poweron;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.settings.newcard.helper.ProgressDialogView;
import com.worldventures.dreamtrips.wallet.util.AnimateGIFUtil;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class NewCardPowerOnScreen extends WalletLinearLayout<NewCardPowerOnPresenter.Screen, NewCardPowerOnPresenter, NewCardPowerOnPath> implements NewCardPowerOnPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.tv_power_on_title) TextView powerOnLabel;
   @InjectView(R.id.wallet_power_on_card) SimpleDraweeView powerOnCardDraweeView;

   private MaterialDialog dontTurnOnCardDialog = null;

   public NewCardPowerOnScreen(Context context) {
      super(context);
   }

   public NewCardPowerOnScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @NonNull
   @Override
   public NewCardPowerOnPresenter createPresenter() {
      return new NewCardPowerOnPresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      supportConnectionStatusLabel(false);
      super.onFinishInflate();
      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      AnimateGIFUtil.setupAnimateGIFbyFresco(powerOnCardDraweeView, R.drawable.animation_power_on);
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   public void setTitleWithSmartCardID(String scID) {
      powerOnLabel.setText(Html.fromHtml(getString(R.string.wallet_new_card_power_on, scID)));
   }

   @OnClick(R.id.btn_next)
   public void onClickNext() {
      presenter.navigateNext();
   }

   @OnClick(R.id.tv_card_not_turn_on)
   public void onClickCardNotTurnOn() {
      presenter.cantTurnOnSmartCard();
   }

   @Override
   public void showConfirmationUnassignOnBackend(String scId) {
      if (dontTurnOnCardDialog == null) {
         dontTurnOnCardDialog = new MaterialDialog.Builder(getContext())
               .content(Html.fromHtml(getString(R.string.wallet_new_card_pre_dont_have_card_msg, scId)))
               .positiveText(R.string.wallet_continue_label)
               .negativeText(R.string.cancel)
               .onPositive((dialog, which) -> presenter.unassignCardOnBackend())
               .build();
      }

      if(!dontTurnOnCardDialog.isShowing()) dontTurnOnCardDialog.show();
   }

   @Override
   public OperationView<WipeSmartCardDataCommand> provideWipeOperationView() {
      return new ComposableOperationView<>(
            ProgressDialogView.<WipeSmartCardDataCommand>builder(getContext()).build(),
            ErrorViewFactory.<WipeSmartCardDataCommand>builder().build()
      );
   }

   @Override
   protected void onDetachedFromWindow() {
      if(dontTurnOnCardDialog != null) dontTurnOnCardDialog.dismiss();
      super.onDetachedFromWindow();
   }
}
