package com.worldventures.wallet.ui.records.add.impl;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.wallet.R;
import com.worldventures.wallet.domain.entity.record.Record;
import com.worldventures.wallet.service.command.record.AddRecordCommand;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.wallet.ui.common.helper2.error.RetryDialogErrorView;
import com.worldventures.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.error.SmartCardErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.wallet.ui.dialog.ChangeDefaultPaymentCardDialog;
import com.worldventures.wallet.ui.records.add.AddCardDetailsPresenter;
import com.worldventures.wallet.ui.records.add.AddCardDetailsScreen;
import com.worldventures.wallet.ui.records.add.RecordBundle;
import com.worldventures.wallet.ui.records.model.RecordViewModel;
import com.worldventures.wallet.ui.widget.BankCardWidget;
import com.worldventures.wallet.ui.widget.PinEntryEditText;
import com.worldventures.wallet.util.CardNameFormatException;
import com.worldventures.wallet.util.CvvFormatException;
import com.worldventures.wallet.util.WalletRecordUtil;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

import static com.worldventures.wallet.util.WalletCardNameUtil.bindSpannableStringToTarget;

public class AddCardDetailsScreenImpl extends WalletBaseController<AddCardDetailsScreen, AddCardDetailsPresenter> implements AddCardDetailsScreen {

   private static final String PARAM_KEY_RECORD_BUNDLE = "AddCardDetailsScreenImpl#PARAM_KEY_RECORD_BUNDLE";
   private static final String STATE_KEY_RECORD_MODEL = "AddCardDetailsScreenImpl#STATE_KEY_RECORD_MODEL";

   @Inject AddCardDetailsPresenter screenPresenter;
   @Inject HttpErrorHandlingUtil httpErrorHandlingUtil;

   private BankCardWidget bankCardWidget;
   private PinEntryEditText etCardCvv;
   private TextView cardNicknameLabel;
   private EditText etCardNickname;
   private TextView cvvLabel;
   private SwitchCompat defaultPaymentCardSwitcher;
   private Button confirmButton;
   private TextInputLayout cardNameInputLayout;

   private Observable<Boolean> setAsDefaultCardObservable;
   private Observable<String> cardNicknameObservable;
   private Observable<String> cvvObservable;

   @Nullable private RecordViewModel recordViewModel = null;

   public static AddCardDetailsScreenImpl create(RecordBundle bundle) {
      final Bundle args = new Bundle();
      args.putParcelable(PARAM_KEY_RECORD_BUNDLE, bundle);
      return new AddCardDetailsScreenImpl(args);
   }

   public AddCardDetailsScreenImpl(Bundle args) {
      super(args);
   }

   @Override
   public AddCardDetailsPresenter getPresenter() {
      return screenPresenter;
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> navigateButtonClick());
      bankCardWidget = view.findViewById(R.id.card);

      etCardCvv = view.findViewById(R.id.card_cvv);
      cvvObservable = observableTextView(etCardCvv);

      cardNicknameLabel = view.findViewById(R.id.card_nickname_label);
      etCardNickname = view.findViewById(R.id.card_name);
      etCardNickname.setOnEditorActionListener((textView, actionId, keyEvent) -> {
         if (actionId == EditorInfo.IME_ACTION_DONE) {
            addRecordWithCurrentData();
            return true;
         }
         return false;
      });
      cardNicknameObservable = observableTextView(etCardNickname)
            .map(String::trim);

      defaultPaymentCardSwitcher = view.findViewById(R.id.set_default_card_switcher);
      setAsDefaultCardObservable = RxCompoundButton.checkedChanges(defaultPaymentCardSwitcher)
            .doOnNext(value -> bankCardWidget.setAsDefault(value));

      confirmButton = view.findViewById(R.id.confirm_button);
      confirmButton.setOnClickListener(button -> addRecordWithCurrentData());
      cardNameInputLayout = view.findViewById(R.id.cardNameInputLayout);
      cvvLabel = view.findViewById(R.id.cvv_label);
      setHintsAndLabels();
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_add_card_details, viewGroup, false);
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      if (recordViewModel == null) {
         screenPresenter.fetchRecordViewModel();
      }
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   private Observable<String> observableTextView(TextView textView) {
      return RxTextView.afterTextChangeEvents(textView)
            .map(TextViewAfterTextChangeEvent::editable)
            .filter(text -> text != null)
            .map(CharSequence::toString);
   }

   @Override
   public void setCardBank(RecordViewModel recordViewModel) {
      this.recordViewModel = recordViewModel;
      bankCardWidget.setBankCard(recordViewModel);
      etCardCvv.setMaxLength(recordViewModel.getCvvLength());
   }

   public Observable<String> getCardNicknameObservable() {
      return cardNicknameObservable.asObservable();
   }

   @Override
   public Observable<String> getCvvObservable() {
      return cvvObservable;
   }

   @Override
   public void setCardName(CharSequence cardName) {
      bankCardWidget.setCardName(cardName);
   }

   @Override
   public void defaultPaymentCard(boolean defaultPaymentCard) {
      defaultPaymentCardSwitcher.setChecked(defaultPaymentCard);
   }

   @Override
   public void showChangeCardDialog(Record record) {
      new ChangeDefaultPaymentCardDialog(getContext(), WalletRecordUtil.Companion.bankNameWithCardNumber(record))
            .setOnCancelAction(() -> getPresenter().onCardToDefaultClick(false))
            .setOnConfirmAction(() -> getPresenter().onCardToDefaultClick(true))
            .show();
   }

   @Override
   public Observable<Boolean> setAsDefaultPaymentCardCondition() {
      return setAsDefaultCardObservable;
   }

   @Override
   public void setEnableButton(boolean enable) {
      confirmButton.setEnabled(enable);
   }

   @Override
   public void showCardNameError() {
      cardNameInputLayout.setError(getString(R.string.wallet_card_details_nickname_error));
   }

   @Override
   public void hideCardNameError() {
      cardNameInputLayout.setError("");
   }

   @Override
   public OperationView<AddRecordCommand> provideOperationAddRecord() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_loading, false),
            ErrorViewFactory.<AddRecordCommand>builder()
                  .defaultErrorView(new RetryDialogErrorView<>(getContext(), R.string.wallet_add_card_details_error_default,
                        command -> addRecordWithCurrentData()))
                  .addProvider(new SmartCardErrorViewProvider<>(getContext(), command -> addRecordWithCurrentData()))
                  .addProvider(new HttpErrorViewProvider<>(getContext(), httpErrorHandlingUtil, command -> addRecordWithCurrentData(), command -> {
                  }))
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), CardNameFormatException.class, R.string.wallet_add_card_details_error_message))
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), CvvFormatException.class, R.string.wallet_add_card_details_error_message))
                  .build()
      );
   }

   @Override
   public RecordBundle getRecordBundle() {
      return getArgs().getParcelable(PARAM_KEY_RECORD_BUNDLE);
   }

   private void navigateButtonClick() {
      getPresenter().goBack();
   }

   private void addRecordWithCurrentData() {
      String cvv = etCardCvv.getText().toString().trim();
      String nickname = etCardNickname.getText().toString().trim();
      boolean setAsDefaultCard = defaultPaymentCardSwitcher.isChecked();

      getPresenter().onCardInfoConfirmed(cvv, nickname, setAsDefaultCard);
   }

   private void setHintsAndLabels() {
      bindSpannableStringToTarget(cvvLabel, R.string.wallet_add_card_details_cvv_label, true, false);
      bindSpannableStringToTarget(etCardNickname, R.string.wallet_add_card_details_hint_nickname_card, true, true);
      bindSpannableStringToTarget(cardNicknameLabel, R.string.wallet_card_details_label_card_nickname,
            R.string.wallet_add_card_details_hint_card_name_length, true, false);
   }

   @Override
   protected void onSaveViewState(@NonNull View view, @NonNull Bundle outState) {
      super.onSaveViewState(view, outState);
      outState.putParcelable(STATE_KEY_RECORD_MODEL, recordViewModel);
   }

   @Override
   protected void onRestoreViewState(@NonNull View view, @NonNull Bundle savedViewState) {
      super.onRestoreViewState(view, savedViewState);
      setCardBank(savedViewState.getParcelable(STATE_KEY_RECORD_MODEL));
      setCardName(etCardNickname.getText());
   }

   @Nullable
   @Override
   protected Object screenModule() {
      return new AddCardDetailsScreenModule();
   }
}
