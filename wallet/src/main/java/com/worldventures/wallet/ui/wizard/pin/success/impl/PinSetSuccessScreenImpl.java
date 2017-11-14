package com.worldventures.wallet.ui.wizard.pin.success.impl;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldventures.wallet.R;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.wizard.pin.Action;
import com.worldventures.wallet.ui.wizard.pin.success.PinSetSuccessPresenter;
import com.worldventures.wallet.ui.wizard.pin.success.PinSetSuccessScreen;

import javax.inject.Inject;

public class PinSetSuccessScreenImpl extends WalletBaseController<PinSetSuccessScreen, PinSetSuccessPresenter> implements PinSetSuccessScreen {

   private static final String KEY_PIN_ACTION = "key_pin_action";

   private TextView nextButton;

   @Inject PinSetSuccessPresenter presenter;

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
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goToBack());
      nextButton = view.findViewById(R.id.next_button);
      nextButton.setOnClickListener(next -> getPresenter().goToNext());
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
