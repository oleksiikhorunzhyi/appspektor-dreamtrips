package com.worldventures.dreamtrips.wallet.ui.wizard.pin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;

public class WizardPinSetupScreen extends WalletFrameLayout<WizardPinSetupPresenter.Screen, WizardPinSetupPresenter, WizardPinSetupPath> implements WizardPinSetupPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.header_text_view) TextView headerTextView;
   @InjectView(R.id.button_next) TextView nextButton;
   private DialogOperationScreen dialogOperationScreen;

   public WizardPinSetupScreen(Context context) {
      super(context);
   }

   public WizardPinSetupScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> onBackClick());
   }

   private void onBackClick() {
      presenter.goToBack();
   }

   @Override
   public void setPath(WizardPinSetupPath path) {
      super.setPath(path);
      if (path.action == WizardPinSetupPath.Action.RESET) {
         headerTextView.setText(R.string.wallet_wizard_setup_new_pin_header);
         nextButton.setText(R.string.wallet_continue_label);
      } else {
         headerTextView.setText(R.string.wallet_wizard_setup_pin_header);
         nextButton.setText(R.string.wallet_got_it_label);
      }
   }

   @NonNull
   @Override
   public WizardPinSetupPresenter createPresenter() {
      return new WizardPinSetupPresenter(getContext(), getInjector(), getPath().smartCard, getPath().action == WizardPinSetupPath.Action.RESET);
   }

   @OnClick(R.id.button_next)
   public void nextClick() {
      presenter.setupPIN();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      if (dialogOperationScreen == null) dialogOperationScreen = new DialogOperationScreen(this);
      return dialogOperationScreen;
   }

}