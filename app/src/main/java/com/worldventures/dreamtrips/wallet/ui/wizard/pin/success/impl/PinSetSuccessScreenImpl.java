package com.worldventures.dreamtrips.wallet.ui.wizard.pin.success.impl;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.success.PinSetSuccessPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.success.PinSetSuccessScreen;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

public class PinSetSuccessScreenImpl extends WalletBaseController<PinSetSuccessScreen, PinSetSuccessPresenter> implements PinSetSuccessScreen{

   private static final String KEY_PIN_ACTION = "key_pin_action";

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.next_button) TextView nextButton;
   @InjectView(R.id.success_label_text_view) TextView successText;

   @Inject PinSetSuccessPresenter presenter;

   private final DialogOperationScreen dialogOperationScreen = new DialogOperationScreen(getView());

   public static PinSetSuccessScreenImpl create(Action pinAction) {
      final Bundle args = new Bundle();
      args.putSerializable(KEY_PIN_ACTION, pinAction);
      return new PinSetSuccessScreenImpl(args);
   }

   public PinSetSuccessScreenImpl() {
      super();
   }

   public PinSetSuccessScreenImpl(Bundle args) {
      super(args);
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goToBack());
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_success, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   @OnClick(R.id.next_button)
   public void nextClick() {
      getPresenter().goToNext();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return dialogOperationScreen;
   }

   @Override
   public void showMode(Action mode) {
      if (mode == Action.RESET) {
         nextButton.setText(R.string.wallet_continue_label);
      } else {
         nextButton.setText(R.string.wallet_done_label);
      }
   }

   @Override
   public Action getPinAction() {
      return (getArgs() != null && !getArgs().isEmpty() && getArgs().containsKey(KEY_PIN_ACTION))
            ? (Action) getArgs().getSerializable(KEY_PIN_ACTION)
            : null;
   }

   @Override
   public PinSetSuccessPresenter getPresenter() {
      return presenter;
   }
}
