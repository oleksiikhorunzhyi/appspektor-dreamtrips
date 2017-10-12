package com.worldventures.dreamtrips.wallet.ui.wizard.input.manual.impl;

import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.service.command.http.GetSmartCardStatusCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.manual.WizardManualInputPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.manual.WizardManualInputScreen;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public class WizardManualInputScreenImpl extends WalletBaseController<WizardManualInputScreen, WizardManualInputPresenter> implements WizardManualInputScreen {

   private EditText scidNumberInput;
   private Button nextButton;

   @Inject WizardManualInputPresenter presenter;
   @Inject HttpErrorHandlingUtil httpErrorHandlingUtil;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      scidNumberInput = view.findViewById(R.id.wallet_wizard_manual_input_scid);
      scidNumberInput.setOnEditorActionListener((textView, action, keyEvent) -> {
         if (action == EditorInfo.IME_ACTION_NEXT) {
            getPresenter().checkBarcode(scidNumberInput.getText().toString());
            return true;
         }
         return false;
      });
      nextButton = view.findViewById(R.id.wallet_wizard_manual_input_next_btn);
      nextButton.setOnClickListener(nextBtn -> getPresenter().checkBarcode(scidNumberInput.getText().toString()));
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_manual_input, viewGroup, false);
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
   public void buttonEnable(boolean isEnable) {
      nextButton.setEnabled(isEnable);
   }

   @NonNull
   @Override
   public Observable<CharSequence> scidInput() {
      return RxTextView.textChanges(scidNumberInput);
   }

   @Override
   public int getScIdLength() {
      return getResources().getInteger(R.integer.wallet_smart_card_id_length);
   }

   @Override
   public OperationView<GetSmartCardStatusCommand> provideOperationFetchCardStatus() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_wizard_assigning_msg, false),
            ErrorViewFactory.<GetSmartCardStatusCommand>builder()
                  .addProvider(new HttpErrorViewProvider<>(getContext(), httpErrorHandlingUtil,
                        command -> getPresenter().retry(command.barcode), c -> { /*nothing*/ })
                  ).build()
      );
   }

   @Override
   public void showErrorCardIsAssignedDialog() {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_wizard_manual_input_card_is_assigned)
            .positiveText(R.string.ok)
            .onPositive((dialog, which) -> dialog.dismiss())
            .show();
   }

   @Override
   public WizardManualInputPresenter getPresenter() {
      return presenter;
   }
}
