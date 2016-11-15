package com.worldventures.dreamtrips.wallet.ui.wizard.card_details;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxCompoundButton;
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
   @InjectView(R.id.card_nickname) EditText cardNameField;

   @InjectView(R.id.set_default_card_switcher) CompoundButton defaultPaymentCardSwitcher;

   private DialogOperationScreen dialogOperationScreen;
   private Observable<Boolean> setAsDefaultCardObservable;

   public AddCardDetailsScreen(Context context) {
      super(context);
   }

   public AddCardDetailsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public AddCardDetailsPresenter createPresenter() {
      return new AddCardDetailsPresenter(getContext(), getInjector(), getPath().getBankCard());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> navigateButtonClick());
      setAsDefaultCardObservable = RxCompoundButton.checkedChanges(defaultPaymentCardSwitcher).skip(1);
   }

   @Override
   public void cardBankInfo(BankCardHelper cardHelper, BankCard bankCard) {
      bankCardWidget.setBankCardInfo(cardHelper, bankCard);

      int cvvLength = BankCardHelper.obtainRequiredCvvLength(bankCard.number());
      cardCvvField.setMaxLength(cvvLength);
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
   public void showChangeCardDialog(@NonNull String bankCardName) {
      new ChangeDefaultPaymentCardDialog(getContext(), bankCardName)
            .setOnCancelAction(() -> getPresenter().defaultCardDialogConfirmed(false))
            .show();
   }

   @Override
   public Observable<Boolean> setAsDefaultPaymentCardCondition() {
      return setAsDefaultCardObservable;
   }

   protected void navigateButtonClick() {
      presenter.goBack();
   }

   @OnEditorAction(R.id.card_nickname)
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

      getPresenter().onCardInfoConfirmed(addressInfo, cvv, nickname, true, true, setAsDefaultCard);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }
}