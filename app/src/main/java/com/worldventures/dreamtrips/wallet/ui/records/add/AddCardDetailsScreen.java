package com.worldventures.dreamtrips.wallet.ui.records.add;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
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
   @InjectView(R.id.card_name) EditText cardNameField;
   @InjectView(R.id.cvv_label) TextView cvvLabel;
   @InjectView(R.id.set_default_card_switcher) CompoundButton defaultPaymentCardSwitcher;
   @InjectView(R.id.confirm_button) View confirmButton;

   private final BankCardHelper bankCardHelper;
   private DialogOperationScreen dialogOperationScreen;

   private Observable<Boolean> setAsDefaultCardObservable;
   private Observable<String> cardNameObservable;
   private Observable<String> address1Observable;
   private Observable<String> stateObservable;
   private Observable<String> cityObservable;
   private Observable<String> zipObservable;

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
      setCvvLabel();
      setCardNameHint();
      setAddressHints();

      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> navigateButtonClick());
      setAsDefaultCardObservable = RxCompoundButton.checkedChanges(defaultPaymentCardSwitcher).skip(1);
      cardNameObservable = observableFrom(cardNameField);

      address1Observable = observableFrom(address1Field);
      stateObservable = observableFrom(stateField);
      cityObservable = observableFrom(cityField);
      zipObservable = observableFrom(zipField);
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

   @Override
   public Observable<String> getCardNameObservable() {
      return cardNameObservable;
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
      String nickname = cardNameField.getText().toString().trim();
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

   private void setCvvLabel() {
      cvvLabel.setText(new SpannableStringBuilder()
            .append(getString(R.string.wallet_add_card_details_cvv_label))
            .append(spannableRequiredFields()));
   }

   private void setCardNameHint() {
      cardNameField.setHint(new SpannableStringBuilder()
            .append(getString(R.string.wallet_add_card_details_hint_card_name))
            .append(spannableRequiredFields())
            .append(" ")
            .append(reduceSizeSpannable(getString(R.string.wallet_add_card_details_hint_card_name_length)))
      );
   }

   private void setAddressHints() {
      address1Field.setHint(new SpannableStringBuilder(getString(R.string.wallet_add_card_details_hint_address1))
            .append(spannableRequiredFields()));
      address2Field.setHint(new SpannableStringBuilder(getString(R.string.wallet_add_card_details_hint_address2_label))
            .append(" ")
            .append(reduceSizeSpannable(getString(R.string.wallet_add_card_details_hint_optional)))
      );
      cityField.setHint(new SpannableStringBuilder(getString(R.string.wallet_add_card_details_hint_city))
            .append(spannableRequiredFields()));
      stateField.setHint(new SpannableStringBuilder(getString(R.string.wallet_add_card_details_hint_state))
            .append(spannableRequiredFields()));
      zipField.setHint(new SpannableStringBuilder(getString(R.string.wallet_add_card_details_hint_zip))
            .append(spannableRequiredFields()));
   }

   private SpannableString spannableRequiredFields() {
      final SpannableString requiredFields = new SpannableString("*");
      requiredFields.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      return requiredFields;
   }

   private SpannableString reduceSizeSpannable(String text) {
      final SpannableString spannableString = new SpannableString(text);
      spannableString.setSpan(new RelativeSizeSpan(.75f), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      return spannableString;
   }
}
