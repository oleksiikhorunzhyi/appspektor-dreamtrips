package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.poweron;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetOperationView;
import com.worldventures.dreamtrips.wallet.ui.widget.WizardVideoView;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class NewCardPowerOnScreen extends WalletLinearLayout<NewCardPowerOnPresenter.Screen, NewCardPowerOnPresenter, NewCardPowerOnPath> implements NewCardPowerOnPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.tv_power_on_title) TextView powerOnLabel;
   @InjectView(R.id.wizard_video_view) WizardVideoView wizardVideoView;

   private MaterialDialog dontTurnOnCardDialog = null;

   public NewCardPowerOnScreen(Context context) {
      super(context);
   }

   public NewCardPowerOnScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
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
      wizardVideoView.setVideoSource(R.raw.anim_power_on_sc);
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
   protected void onDetachedFromWindow() {
      if (dontTurnOnCardDialog != null) dontTurnOnCardDialog.dismiss();
      super.onDetachedFromWindow();
   }

   @Override
   public View getView() {
      return this;
   }

   @Override
   public OperationView<ResetSmartCardCommand> provideResetOperationView(FactoryResetDelegate factoryResetDelegate) {
      return FactoryResetOperationView.create(getContext(),
            factoryResetDelegate::factoryReset,
            () -> {},
            R.string.wallet_error_enter_pin_title,
            R.string.wallet_error_enter_pin_msg,
            R.string.retry,
            R.string.cancel,
            R.string.loading,
            false);
   }
}
