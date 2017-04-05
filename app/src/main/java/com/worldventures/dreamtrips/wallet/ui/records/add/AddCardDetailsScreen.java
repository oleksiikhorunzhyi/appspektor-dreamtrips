package com.worldventures.dreamtrips.wallet.ui.records.add;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.command.GetDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.AddRecordCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.DialogErrorView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.dialog.ChangeDefaultPaymentCardDialog;
import com.worldventures.dreamtrips.wallet.ui.widget.BankCardWidget;
import com.worldventures.dreamtrips.wallet.ui.widget.PinEntryEditText;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletSwitcher;
import com.worldventures.dreamtrips.wallet.util.AddressFormatException;
import com.worldventures.dreamtrips.wallet.util.CardNameFormatException;
import com.worldventures.dreamtrips.wallet.util.CvvFormatException;
import com.worldventures.dreamtrips.wallet.util.WalletRecordUtil;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

import static com.worldventures.dreamtrips.wallet.util.WalletCardNameUtil.bindSpannableStringToTarget;

public class AddCardDetailsScreen extends WalletLinearLayout<AddCardDetailsPresenter.Screen, AddCardDetailsPresenter, AddCardDetailsPath>
      implements AddCardDetailsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.card) BankCardWidget bankCardWidget;
   @InjectView(R.id.card_cvv) PinEntryEditText etCardCvv;
   @InjectView(R.id.address1) EditText etAddress1;
   @InjectView(R.id.address2) EditText etAddress2;
   @InjectView(R.id.city) EditText etCity;
   @InjectView(R.id.state) EditText etState;
   @InjectView(R.id.zip) EditText etZip;
   @InjectView(R.id.card_nickname_label) TextView cardNicknameLabel;
   @InjectView(R.id.card_name) EditText etCardNickname;
   @InjectView(R.id.cvv_label) TextView cvvLabel;
   @InjectView(R.id.set_default_card_switcher) WalletSwitcher defaultPaymentCardSwitcher;
   @InjectView(R.id.confirm_button) View confirmButton;
   @InjectView(R.id.cardNameInputLayout) TextInputLayout cardNameInputLayout;

   private final WalletRecordUtil walletRecordUtil;

   private Observable<Boolean> setAsDefaultCardObservable;
   private Observable<String> cardNicknameObservable;
   private Observable<String> address1Observable;
   private Observable<String> stateObservable;
   private Observable<String> cityObservable;
   private Observable<String> zipObservable;
   private Observable<String> cvvObservable;

   public AddCardDetailsScreen(Context context) {
      this(context, null);
   }

   public AddCardDetailsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
      walletRecordUtil = new WalletRecordUtil(context);
   }

   @NonNull
   @Override
   public AddCardDetailsPresenter createPresenter() {
      return new AddCardDetailsPresenter(getContext(), getInjector(), getPath().getRecord());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      setHintsAndLabels();

      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> navigateButtonClick());
      setAsDefaultCardObservable = RxCompoundButton.checkedChanges(defaultPaymentCardSwitcher).skip(1);
      cardNicknameObservable = observableFrom(etCardNickname);

      address1Observable = observableFrom(etAddress1);
      stateObservable = observableFrom(etState);
      cityObservable = observableFrom(etCity);
      zipObservable = observableFrom(etZip);
      cvvObservable = observableFrom(etCardCvv);
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
   public Observable<String> getAddress1Observable() {
      return address1Observable;
   }

   @Override
   public Observable<String> getStateObservable() {
      return stateObservable;
   }

   @Override
   public Observable<String> getZipObservable() {
      return zipObservable;
   }

   @Override
   public Observable<String> getCityObservable() {
      return cityObservable;
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
   public void defaultAddress(AddressInfo addressInfo) {
      etAddress1.setText(addressInfo.address1());
      etAddress1.setSelection(etAddress1.length());

      etAddress2.setText(addressInfo.address2());
      etAddress2.setSelection(etAddress2.length());

      etCity.setText(addressInfo.city());
      etCity.setSelection(etCity.length());

      etState.setText(addressInfo.state());
      etState.setSelection(etState.length());

      etZip.setText(addressInfo.zip());
      etZip.setSelection(etZip.length());
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
   public OperationView<GetDefaultAddressCommand> provideOperationGetDefaultAddress() {
      return new ComposableOperationView<>(new SimpleDialogProgressView<>(getContext(), R.string.loading, false));
   }

   @Override
   public OperationView<AddRecordCommand> provideOperationAddRecord() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.loading, false),
            ErrorViewFactory.<AddRecordCommand>builder()
                  .defaultErrorView(getDefaultErrorDialogProvider())
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), CardNameFormatException.class, R.string.wallet_add_card_details_error_message))
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), CvvFormatException.class, R.string.wallet_add_card_details_error_message))
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), AddressFormatException.class, R.string.wallet_add_card_details_error_message))
                  .build()
      );
   }

   @NonNull
   private DialogErrorView<AddRecordCommand> getDefaultErrorDialogProvider() {
      return new DialogErrorView<AddRecordCommand>(getContext()) {
         @Override
         protected MaterialDialog createDialog(AddRecordCommand command, Throwable throwable, Context context) {
            return new MaterialDialog.Builder(getContext())
                  .content(R.string.wallet_add_card_details_error_default)
                  .positiveText(R.string.retry)
                  .onPositive((dialog, which) -> addRecordWithCurrentData())
                  .negativeText(R.string.cancel)
                  .build();
         }
      };
   }

   protected void navigateButtonClick() {
      presenter.goBack();
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

      AddressInfo addressInfo = ImmutableAddressInfo.builder()
            .address1(etAddress1.getText().toString().trim())
            .address2(etAddress2.getText().toString().trim())
            .city(etCity.getText().toString().trim())
            .state(etState.getText().toString().trim())
            .zip(etZip.getText().toString().trim())
            .build();

      getPresenter().onCardInfoConfirmed(addressInfo, cvv, nickname, setAsDefaultCard);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   private void setHintsAndLabels() {
      bindSpannableStringToTarget(cvvLabel, R.string.wallet_add_card_details_cvv_label, true, false);
      bindSpannableStringToTarget(etCardNickname, R.string.wallet_add_card_details_hint_nickname_card, true, true);
      bindSpannableStringToTarget(etAddress1, R.string.wallet_add_card_details_hint_address1, true, true);
      bindSpannableStringToTarget(etAddress2, R.string.wallet_add_card_details_hint_address2_label,
            R.string.wallet_add_card_details_hint_optional, false, true);
      bindSpannableStringToTarget(etCity, R.string.wallet_add_card_details_hint_city, true, true);
      bindSpannableStringToTarget(etState, R.string.wallet_add_card_details_hint_state, true, true);
      bindSpannableStringToTarget(etZip, R.string.wallet_add_card_details_hint_zip, true, true);
      bindSpannableStringToTarget(cardNicknameLabel, R.string.wallet_card_details_label_card_nickname,
            R.string.wallet_add_card_details_hint_card_name_length, true, false);
   }
}
