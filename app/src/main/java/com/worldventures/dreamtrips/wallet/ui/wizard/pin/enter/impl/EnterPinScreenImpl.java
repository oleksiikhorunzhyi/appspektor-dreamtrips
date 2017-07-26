package com.worldventures.dreamtrips.wallet.ui.wizard.pin.enter.impl;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SCConnectionErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleErrorView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SmartCardErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.widget.WizardVideoView;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.enter.EnterPinPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.enter.EnterPinScreen;

import javax.inject.Inject;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class EnterPinScreenImpl extends WalletBaseController<EnterPinScreen, EnterPinPresenter> implements EnterPinScreen {

   private static final String KEY_PIN_ACTION = "key_pin_action";

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.header_text_view) TextView headerTextView;
   @InjectView(R.id.wizard_video_view) WizardVideoView wizardVideoView;

   @Inject EnterPinPresenter presenter;

   private DialogOperationScreen dialogOperationScreen;
   private MaterialDialog infoLockGesturesDialog = null;

   public static EnterPinScreenImpl create(Action pinAction) {
      final Bundle args = new Bundle();
      args.putSerializable(KEY_PIN_ACTION, pinAction);
      return new EnterPinScreenImpl(args);
   }

   public EnterPinScreenImpl() {
      super();
   }

   public EnterPinScreenImpl(Bundle args) {
      super(args);
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> onBackClick());
      setupMenuItem();
      headerTextView.setText(R.string.wallet_wizard_setup_pin_header);
      wizardVideoView.setVideoSource(R.raw.anim_pin_entry);
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_pin_setup, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
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
      if (!infoLockGesturesDialog.isShowing()) infoLockGesturesDialog.show();
   }

   private void onBackClick() {
      getPresenter().goBack();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      if (dialogOperationScreen == null) dialogOperationScreen = new DialogOperationScreen(getView());
      return dialogOperationScreen;
   }

   @Override
   protected void onDetach(@NonNull View view) {
      if (infoLockGesturesDialog != null) infoLockGesturesDialog.dismiss();
      super.onDetach(view);
   }

   @Override
   public EnterPinPresenter getPresenter() {
      return presenter;
   }

   @Override
   public void addMode() {
   }

   @Override
   public void setupMode() {
//      TODO : resolve this implementation
//      supportConnectionStatusLabel(false);
   }

   @Override
   public void resetMode() {
   }

   @Override
   public <T> OperationView<T> operationView() {
      return new ComposableOperationView<>(ErrorViewFactory.<T>builder()
            .addProvider(new SCConnectionErrorViewProvider<>(getContext(), t -> getPresenter().retry(), t -> getPresenter().goBack()))
            .addProvider(new SmartCardErrorViewProvider<>(getContext(), t -> getPresenter().goBack()))
            .defaultErrorView(new SimpleErrorView<>(getContext(), getString(R.string.wallet_wizard_setup_error), t -> getPresenter().goBack()))
            .build());
   }

   @Override
   public Action getPinAction() {
      return (getArgs() != null && !getArgs().isEmpty() && getArgs().containsKey(KEY_PIN_ACTION))
            ? (Action) getArgs().getSerializable(KEY_PIN_ACTION)
            : null;
   }

   @Override
   public boolean handleBack() {
      getPresenter().cancelSetupPIN();
      return super.handleBack();
   }
}
