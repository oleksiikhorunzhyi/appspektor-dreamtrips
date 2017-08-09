package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.poweron.impl;


import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.poweron.NewCardPowerOnPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.poweron.NewCardPowerOnScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetOperationView;
import com.worldventures.dreamtrips.wallet.ui.widget.WizardVideoView;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class NewCardPowerOnScreenImpl extends WalletBaseController<NewCardPowerOnScreen, NewCardPowerOnPresenter> implements NewCardPowerOnScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.tv_power_on_title) TextView powerOnLabel;
   @InjectView(R.id.wizard_video_view) WizardVideoView wizardVideoView;

   @Inject NewCardPowerOnPresenter presenter;

   private MaterialDialog dontTurnOnCardDialog = null;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      wizardVideoView.setVideoSource(R.raw.wallet_anim_power_on_sc);
   }

   @Override
   public void setTitleWithSmartCardID(String scID) {
      powerOnLabel.setText(Html.fromHtml(getString(R.string.wallet_new_card_power_on, scID)));
   }

   @OnClick(R.id.btn_next)
   public void onClickNext() {
      getPresenter().navigateNext();
   }

   @OnClick(R.id.tv_card_not_turn_on)
   public void onClickCardNotTurnOn() {
      getPresenter().cantTurnOnSmartCard();
   }

   @Override
   public void showConfirmationUnassignOnBackend(String scId) {
      if (dontTurnOnCardDialog == null) {
         dontTurnOnCardDialog = new MaterialDialog.Builder(getContext())
               .content(Html.fromHtml(getString(R.string.wallet_new_card_pre_dont_have_card_msg, scId)))
               .positiveText(R.string.wallet_continue_label)
               .negativeText(R.string.cancel)
               .onPositive((dialog, which) -> getPresenter().unassignCardOnBackend())
               .build();
      }

      if (!dontTurnOnCardDialog.isShowing()) dontTurnOnCardDialog.show();
   }

   @Override
   public OperationView<WipeSmartCardDataCommand> provideWipeOperationView() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.loading, true),
            ErrorViewFactory.<WipeSmartCardDataCommand>builder().build()
      );
   }

   @Override
   protected void onDetach(@NonNull View view) {
      if (dontTurnOnCardDialog != null) dontTurnOnCardDialog.dismiss();
      super.onDetach(view);
   }

   @Override
   public OperationView<ResetSmartCardCommand> provideResetOperationView(FactoryResetDelegate factoryResetDelegate) {
      return FactoryResetOperationView.create(getContext(),
            factoryResetDelegate::factoryReset,
            () -> {
            },
            R.string.wallet_error_enter_pin_title,
            R.string.wallet_error_enter_pin_msg,
            R.string.retry,
            R.string.cancel,
            R.string.loading,
            false);
   }

   @Override
   public NewCardPowerOnPresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_new_card_power_on, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }
}
