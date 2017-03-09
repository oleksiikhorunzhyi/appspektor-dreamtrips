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
   @InjectView(R.id.card_cvv) PinEntryEditText etCardCvv;
   @InjectView(R.id.address1) EditText etAddress1;
   @InjectView(R.id.address2) EditText etAddress2;
   @InjectView(R.id.city) EditText etCity;
   @InjectView(R.id.state) EditText etState;
   @InjectView(R.id.zip) EditText etZip;
   @InjectView(R.id.card_name) EditText etCardNickname;
   @InjectView(R.id.cvv_label) TextView cvvLabel;
   @InjectView(R.id.set_default_card_switcher) CompoundButton defaultPaymentCardSwitcher;
   @InjectView(R.id.confirm_button) View confirmButton;
   @InjectView(R.id.tvPostDataError) View tvPostDataError;

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
   public void setCardBank(BankCard bankCard) {
      bankCardWidget.setBankCard(bankCard);

      int cvvLength = BankCardHelper.obtainRequiredCvvLength(bankCard.number());
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
   public void defaultAddress(AddressInfoWithLocale defaultAddress) {
      final AddressInfo addressInfo = defaultAddress.addressInfo();
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

   @Override
   public void showPushCardError() {
      tvPostDataError.setVisibility(VISIBLE);
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
      tvPostDataError.setVisibility(GONE);

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
   }
}
