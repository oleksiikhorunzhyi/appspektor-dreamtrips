package com.worldventures.dreamtrips.wallet.ui.records.add.impl;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.command.record.AddRecordCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.RetryDialogErrorView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SmartCardErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.dialog.ChangeDefaultPaymentCardDialog;
import com.worldventures.dreamtrips.wallet.ui.records.add.AddCardDetailsPresenter;
import com.worldventures.dreamtrips.wallet.ui.records.add.AddCardDetailsScreen;
import com.worldventures.dreamtrips.wallet.ui.records.model.RecordViewModel;
import com.worldventures.dreamtrips.wallet.ui.widget.BankCardWidget;
import com.worldventures.dreamtrips.wallet.ui.widget.PinEntryEditText;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletSwitcher;
import com.worldventures.dreamtrips.wallet.util.CardNameFormatException;
import com.worldventures.dreamtrips.wallet.util.CvvFormatException;
import com.worldventures.dreamtrips.wallet.util.WalletRecordUtil;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

import static com.worldventures.dreamtrips.wallet.util.WalletCardNameUtil.bindSpannableStringToTarget;

public class AddCardDetailsScreenImpl extends WalletBaseController<AddCardDetailsScreen, AddCardDetailsPresenter> implements AddCardDetailsScreen {

   private static final String KEY_ADD_RECORD = "key_add_record";

   @Inject AddCardDetailsPresenter presenter;

   private BankCardWidget bankCardWidget;
   private PinEntryEditText etCardCvv;
   private TextView cardNicknameLabel;
   private EditText etCardNickname;
   private TextView cvvLabel;
   private WalletSwitcher defaultPaymentCardSwitcher;
   private Button confirmButton;
   private TextInputLayout cardNameInputLayout;

   private Observable<Boolean> setAsDefaultCardObservable;
   private Observable<String> cardNicknameObservable;
   private Observable<String> cvvObservable;

   public static AddCardDetailsScreenImpl create(RecordViewModel recordViewModel) {
      final Bundle args = new Bundle();
      args.putParcelable(KEY_ADD_RECORD, recordViewModel);
      return new AddCardDetailsScreenImpl(args);
   }

   public AddCardDetailsScreenImpl(Bundle args) {
      super(args);
   }

   @Override
   public AddCardDetailsPresenter getPresenter() {
      return presenter;
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> navigateButtonClick());
      bankCardWidget = view.findViewById(R.id.card);

      etCardCvv = view.findViewById(R.id.card_cvv);
      cvvObservable = observableFrom(etCardCvv);

      cardNicknameLabel = view.findViewById(R.id.card_nickname_label);
      etCardNickname = view.findViewById(R.id.card_name);
      etCardNickname.setOnEditorActionListener((textView, actionId, keyEvent) -> {
         if (actionId == EditorInfo.IME_ACTION_DONE) {
            addRecordWithCurrentData();
            return true;
         }
         return false;
      });
      cardNicknameObservable = RxTextView.afterTextChangeEvents(etCardNickname).map(event -> event.editable()
            .toString()
            .trim()).skip(1);

      defaultPaymentCardSwitcher = view.findViewById(R.id.set_default_card_switcher);
      setAsDefaultCardObservable = RxCompoundButton.checkedChanges(defaultPaymentCardSwitcher)
            .doOnNext(value -> bankCardWidget.setAsDefault(value))
            .skip(1);

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
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   private Observable<String> observableFrom(TextView textView) {
      return RxTextView.afterTextChangeEvents(textView).map(event -> event.editable().toString());
   }

   @Override
   public void setCardBank(RecordViewModel recordViewModel) {
      bankCardWidget.setBankCard(recordViewModel);
      etCardCvv.setMaxLength(recordViewModel.getCvvLength());
   }

   public Observable<String> getCardNicknameObservable() {
      return cardNicknameObservable;
   }

   @Override
   public Observable<String> getCvvObservable() {
      return cvvObservable;
   }

   @Override
   public void setCardName(String cardName) {
      bankCardWidget.setCardName(cardName);
   }

   @Override
   public void defaultPaymentCard(boolean defaultPaymentCard) {
      defaultPaymentCardSwitcher.setChecked(defaultPaymentCard);
   }

   @Override
   public void showChangeCardDialog(Record record) {
      new ChangeDefaultPaymentCardDialog(getContext(), WalletRecordUtil.bankNameWithCardNumber(record))
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
            new SimpleDialogProgressView<>(getContext(), R.string.loading, false),
            ErrorViewFactory.<AddRecordCommand>builder()
                  .defaultErrorView(new RetryDialogErrorView<>(getContext(), R.string.wallet_add_card_details_error_default,
                        command -> addRecordWithCurrentData()))
                  .addProvider(new SmartCardErrorViewProvider<>(getContext(), command -> addRecordWithCurrentData()))
                  .addProvider(new HttpErrorViewProvider<>(getContext(), getPresenter().httpErrorHandlingUtil(), command -> addRecordWithCurrentData(), command -> {
                  }))
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), CardNameFormatException.class, R.string.wallet_add_card_details_error_message))
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), CvvFormatException.class, R.string.wallet_add_card_details_error_message))
                  .build()
      );
   }

   @Override
   public RecordViewModel getRecordViewModel() {
      return (getArgs() != null && !getArgs().isEmpty() && getArgs().containsKey(KEY_ADD_RECORD))
            ? getArgs().getParcelable(KEY_ADD_RECORD)
            : null;
   }

   protected void navigateButtonClick() {
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
}