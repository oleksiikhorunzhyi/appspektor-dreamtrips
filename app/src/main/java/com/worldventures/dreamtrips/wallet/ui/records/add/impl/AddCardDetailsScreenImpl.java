package com.worldventures.dreamtrips.wallet.ui.records.add.impl;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.command.record.AddRecordCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.RetryDialogErrorView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SmartCardErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.dialog.ChangeDefaultPaymentCardDialog;
import com.worldventures.dreamtrips.wallet.ui.records.add.AddCardDetailsPresenter;
import com.worldventures.dreamtrips.wallet.ui.records.add.AddCardDetailsScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.BankCardWidget;
import com.worldventures.dreamtrips.wallet.ui.widget.PinEntryEditText;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletSwitcher;
import com.worldventures.dreamtrips.wallet.util.CardNameFormatException;
import com.worldventures.dreamtrips.wallet.util.CvvFormatException;
import com.worldventures.dreamtrips.wallet.util.WalletRecordUtil;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

import static com.worldventures.dreamtrips.wallet.util.WalletCardNameUtil.bindSpannableStringToTarget;

public class AddCardDetailsScreenImpl extends WalletBaseController<AddCardDetailsScreen, AddCardDetailsPresenter> implements AddCardDetailsScreen {

   private static final String KEY_ADD_RECORD = "key_add_record";

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.card) BankCardWidget bankCardWidget;
   @InjectView(R.id.card_cvv) PinEntryEditText etCardCvv;
   @InjectView(R.id.card_nickname_label) TextView cardNicknameLabel;
   @InjectView(R.id.card_name) EditText etCardNickname;
   @InjectView(R.id.cvv_label) TextView cvvLabel;
   @InjectView(R.id.set_default_card_switcher) WalletSwitcher defaultPaymentCardSwitcher;
   @InjectView(R.id.confirm_button) View confirmButton;
   @InjectView(R.id.cardNameInputLayout) TextInputLayout cardNameInputLayout;

   @Inject AddCardDetailsPresenter presenter;

   private final WalletRecordUtil walletRecordUtil;

   private Observable<Boolean> setAsDefaultCardObservable;
   private Observable<String> cardNicknameObservable;
   private Observable<String> cvvObservable;

   public static AddCardDetailsScreenImpl create(Record record) {
      final Bundle args = new Bundle();
      //TODO : deal with Parcelable
      args.putParcelable(KEY_ADD_RECORD, (Parcelable) record);
      return new AddCardDetailsScreenImpl();
   }

   public AddCardDetailsScreenImpl() {
      this(null);
   }

   public AddCardDetailsScreenImpl(Bundle args) {
      super(args);
      this.walletRecordUtil = new WalletRecordUtil();
   }

   @Override
   public AddCardDetailsPresenter getPresenter() {
      return presenter;
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      setHintsAndLabels();
      toolbar.setNavigationOnClickListener(v -> navigateButtonClick());
      setAsDefaultCardObservable = RxCompoundButton.checkedChanges(defaultPaymentCardSwitcher).skip(1);
      cardNicknameObservable = observableFrom(etCardNickname);
      cvvObservable = observableFrom(etCardCvv);
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
   public void setCardBank(Record record) {
      bankCardWidget.setBankCard(record);

      int cvvLength = WalletRecordUtil.obtainRequiredCvvLength(record.number());
      etCardCvv.setMaxLength(cvvLength);
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
   public OperationScreen provideOperationDelegate() {return null;}

   @Override
   public void showChangeCardDialog(Record record) {
      new ChangeDefaultPaymentCardDialog(getContext(), walletRecordUtil.bankNameWithCardNumber(record))
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
                  .addProvider(new HttpErrorViewProvider<>(getContext(), getPresenter().httpErrorHandlingUtil(), command -> addRecordWithCurrentData(), command -> { }))
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), CardNameFormatException.class, R.string.wallet_add_card_details_error_message))
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), CvvFormatException.class, R.string.wallet_add_card_details_error_message))
                  .build()
      );
   }

   @Override
   public Record getRecord() {
      return (getArgs() != null && !getArgs().isEmpty() && getArgs().containsKey(KEY_ADD_RECORD))
            ? getArgs().getParcelable(KEY_ADD_RECORD)
            : null;
   }

   protected void navigateButtonClick() {
      getPresenter().goBack();
   }

   @OnEditorAction(R.id.card_name)
   boolean onEditorAction(int action) {
      if (action == EditorInfo.IME_ACTION_DONE) {
         onConfirmButtonClicked();
         return true;
      }
      return false;
   }

   @OnClick(R.id.confirm_button)
   public void onConfirmButtonClicked() {
      addRecordWithCurrentData();
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
