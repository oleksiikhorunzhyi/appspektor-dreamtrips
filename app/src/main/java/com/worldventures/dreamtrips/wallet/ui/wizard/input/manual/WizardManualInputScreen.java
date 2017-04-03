package com.worldventures.dreamtrips.wallet.ui.wizard.input.manual;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.http.GetSmartCardStatusCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public class WizardManualInputScreen extends WalletLinearLayout<WizardManualInputPresenter.Screen, WizardManualInputPresenter, WizardManualInputPath> implements WizardManualInputPresenter.Screen {
   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.wallet_wizard_manual_input_scid) EditText scidNumberInput;
   @InjectView(R.id.wallet_wizard_manual_input_next_btn) View nextButton;

   private MaterialDialog errorCardIsAssignedDialog = null;

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
   public OperationScreen provideOperationDelegate() { return null; }

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

   @Override
   public OperationView<GetSmartCardStatusCommand> provideOperationFetchCardStatus() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_wizard_assigning_msg, false),
            ErrorViewFactory.<GetSmartCardStatusCommand>builder()
                  .addProvider(new HttpErrorViewProvider<>(getContext(),
                        command -> presenter.checkBarcode(scidNumberInput.getText().toString()),
                        null)
                  ).build()
      );
   }

   @Override
   public void showErrorCardIsAssignedDialog() {
      if (errorCardIsAssignedDialog == null) {
         errorCardIsAssignedDialog = new MaterialDialog.Builder(getContext())
               .content(R.string.wallet_wizard_manual_input_card_is_assigned)
               .positiveText(R.string.ok)
               .onPositive((dialog, which) -> dialog.dismiss())
               .build();
      }
      if (!errorCardIsAssignedDialog.isShowing()) errorCardIsAssignedDialog.show();
   }

   @Override
   protected void onDetachedFromWindow() {
      if (errorCardIsAssignedDialog != null) errorCardIsAssignedDialog.dismiss();
      super.onDetachedFromWindow();
   }
}