package com.worldventures.dreamtrips.wallet.ui.wizard.pin.setup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;

import butterknife.InjectView;
import butterknife.OnClick;

public class WizardPinSetupScreen extends WalletLinearLayout<WizardPinSetupPresenter.Screen, WizardPinSetupPresenter, WizardPinSetupPath> implements WizardPinSetupPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.header_text_view) TextView headerTextView;
   @InjectView(R.id.button_next) TextView nextButton;

   private DialogOperationScreen dialogOperationScreen;
   private MaterialDialog infoLockGesturesDialog = null;

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
      setupMenuItem();
      headerTextView.setText(R.string.wallet_wizard_setup_pin_header);
   }

   private void setupMenuItem() {
      toolbar.inflateMenu(R.menu.menu_wallet_pin_setup);
      toolbar.setOnMenuItemClickListener(item -> handleActionItemsClick(item.getItemId()));
   }

   private boolean handleActionItemsClick(int itemId) {
      switch (itemId) {
         case R.id.action_info_gestures:
            showLockGesturesInfoDialog();
            return true;
      }
      return false;
   }

   private void showLockGesturesInfoDialog() {
      if (infoLockGesturesDialog == null) {
         infoLockGesturesDialog = new MaterialDialog.Builder(getContext())
               .title(R.string.wallet_gestures_info_dialog_title)
               .customView(R.layout.view_pin_gestures_info, true)
               .positiveText(R.string.wallet_close)
               .build();
      }
      if(!infoLockGesturesDialog.isShowing()) infoLockGesturesDialog.show();
   }

   private void onBackClick() {
      presenter.goBack();
   }

   @Override
   public void showMode(Action mode) {
      if (mode == Action.RESET) {
         nextButton.setText(R.string.wallet_continue_label);
      } else {
         nextButton.setText(R.string.wallet_got_it_label);
         supportConnectionStatusLabel(false);
      }
   }

   @NonNull
   @Override
   public WizardPinSetupPresenter createPresenter() {
      return new WizardPinSetupPresenter(getContext(), getInjector(), getPath().action);
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

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @Override
   protected void onDetachedFromWindow() {
      if(infoLockGesturesDialog != null) infoLockGesturesDialog.dismiss();
      super.onDetachedFromWindow();
   }
}