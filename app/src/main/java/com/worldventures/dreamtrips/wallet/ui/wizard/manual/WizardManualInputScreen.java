package com.worldventures.dreamtrips.wallet.ui.wizard.manual;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.Observable;

public class WizardManualInputScreen extends WalletLinearLayout<WizardManualInputPresenter.Screen, WizardManualInputPresenter, WizardManualInputPath> implements WizardManualInputPresenter.Screen {
   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.wallet_wizard_manual_input_scid) EditText scidNumberInput;
   @InjectView(R.id.wallet_wizard_manual_input_next_btn) View nextButton;

   public WizardManualInputScreen(Context context) {
      super(context);
   }

   public WizardManualInputScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
   }

   @NonNull
   @Override
   public WizardManualInputPresenter createPresenter() {
      return new WizardManualInputPresenter(getContext(), getInjector());
   }

   @OnEditorAction(R.id.wallet_wizard_manual_input_scid)
   boolean actionNext(int action) {
      if (action == EditorInfo.IME_ACTION_NEXT) {
         getPresenter().checkBarcode(scidNumberInput.getText().toString());
         return true;
      }
      return false;
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @OnClick(R.id.wallet_wizard_manual_input_next_btn)
   void onNextClicked() {
      getPresenter().checkBarcode(scidNumberInput.getText().toString());
   }

   @Override
   public void buttonEnable(boolean isEnable) {
      nextButton.setEnabled(isEnable);
   }

   @NonNull
   @Override
   public Observable<CharSequence> scidInput() {
      return RxTextView.textChanges(scidNumberInput);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }
}