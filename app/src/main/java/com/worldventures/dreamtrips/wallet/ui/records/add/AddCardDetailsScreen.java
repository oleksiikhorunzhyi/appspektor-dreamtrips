package com.worldventures.dreamtrips.wallet.ui.records.add;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.dialog.ChangeDefaultPaymentCardDialog;
import com.worldventures.dreamtrips.wallet.ui.widget.BankCardWidget;
import com.worldventures.dreamtrips.wallet.ui.widget.PinEntryEditText;
import com.worldventures.dreamtrips.wallet.util.BankCardHelper;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.Observable;

import static com.worldventures.dreamtrips.wallet.util.WalletCardNameUtil.bindSpannableStringToTarget;

public class AddCardDetailsScreen extends WalletLinearLayout<AddCardDetailsPresenter.Screen, AddCardDetailsPresenter, AddCardDetailsPath>
      implements AddCardDetailsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.card) BankCardWidget bankCardWidget;
   @InjectView(R.id.card_cvv) PinEntryEditText cardCvvField;
   @InjectView(R.id.address1) EditText address1Field;
   @InjectView(R.id.address2) EditText address2Field;
   @InjectView(R.id.city) EditText cityField;
   @InjectView(R.id.state) EditText stateField;
   @InjectView(R.id.zip) EditText zipField;
   @InjectView(R.id.card_name) EditText cardNicknameField;
   @InjectView(R.id.cvv_label) TextView cvvLabel;
   @InjectView(R.id.set_default_card_switcher) CompoundButton defaultPaymentCardSwitcher;
   @InjectView(R.id.confirm_button) View confirmButton;

   private final BankCardHelper bankCardHelper;
   private DialogOperationScreen dialogOperationScreen;

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
      bankCardHelper = new BankCardHelper(context);
   }

   @NonNull
   @Override
   public AddCardDetailsPresenter createPresenter() {
      return new AddCardDetailsPresenter(getContext(), getInjector(), getPath().getBankCard());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      setHintsAndLabels();

      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> navigateButtonClick());
      setAsDefaultCardObservable = RxCompoundButton.checkedChanges(defaultPaymentCardSwitcher).skip(1);
      cardNicknameObservable = observableFrom(cardNicknameField);

      address1Observable = observableFrom(address1Field);
      stateObservable = observableFrom(stateField);
      cityObservable = observableFrom(cityField);
      zipObservable = observableFrom(zipField);
      cvvObservable = observableFrom(cardCvvField);
   }

   private Observable<String> observableFrom(TextView textView) {
      return RxTextView.afterTextChangeEvents(textView).map(event -> event.editable().toString()).skip(1);
   }

   @Override
   public void setCardBank(BankCard bankCard) {
      bankCardWidget.setBankCard(bankCard);

      int cvvLength = BankCardHelper.obtainRequiredCvvLength(bankCard.number());
      cardCvvField.setMaxLength(cvvLength);
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
   public void defaultAddress(AddressInfoWithLocale defaultAddress) {
      AddressInfo addressInfo = defaultAddress.addressInfo();
      address1Field.setText(addressInfo.address1());
      address2Field.setText(addressInfo.address2());
      cityField.setText(addressInfo.city());
      stateField.setText(addressInfo.state());
      zipField.setText(addressInfo.zip());
   }

   @Override
   public void defaultPaymentCard(boolean defaultPaymentCard) {
      defaultPaymentCardSwitcher.setChecked(defaultPaymentCard);
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      if (dialogOperationScreen == null) dialogOperationScreen = new DialogOperationScreen(this);
      return dialogOperationScreen;
   }

   @Override
   public void showChangeCardDialog(BankCard bankCard) {
      new ChangeDefaultPaymentCardDialog(getContext(), bankCardHelper.bankNameWithCardNumber(bankCard))
            .setOnCancelAction(() -> getPresenter().defaultCardDialogConfirmed(false))
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
      String cvv = cardCvvField.getText().toString().trim();
      String nickname = cardNicknameField.getText().toString().trim();
      boolean setAsDefaultCard = defaultPaymentCardSwitcher.isChecked();

      AddressInfo addressInfo = ImmutableAddressInfo.builder()
            .address1(address1Field.getText().toString().trim())
            .address2(address2Field.getText().toString().trim())
            .city(cityField.getText().toString().trim())
            .state(stateField.getText().toString().trim())
            .zip(zipField.getText().toString().trim())
            .build();

      getPresenter().onCardInfoConfirmed(addressInfo, cvv, nickname, setAsDefaultCard);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   private void setHintsAndLabels() {
      bindSpannableStringToTarget(cvvLabel, R.string.wallet_add_card_details_cvv_label, true, false);
      bindSpannableStringToTarget(cardNicknameField, R.string.wallet_add_card_details_hint_nickname_card, true, true);
      bindSpannableStringToTarget(address1Field, R.string.wallet_add_card_details_hint_address1, true, true);
      bindSpannableStringToTarget(address2Field, R.string.wallet_add_card_details_hint_address2_label,
            R.string.wallet_add_card_details_hint_optional, false, true);
      bindSpannableStringToTarget(cityField, R.string.wallet_add_card_details_hint_city, true, true);
      bindSpannableStringToTarget(stateField, R.string.wallet_add_card_details_hint_state, true, true);
      bindSpannableStringToTarget(zipField, R.string.wallet_add_card_details_hint_zip, true, true);
   }
}
