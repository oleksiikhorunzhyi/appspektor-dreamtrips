package com.worldventures.dreamtrips.wallet.ui.wizard.card_details;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.BankCardWidget;
import com.worldventures.dreamtrips.wallet.util.AddressUtil;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class AddCardDetailsScreen extends WalletFrameLayout<AddCardDetailsPresenter.Screen, AddCardDetailsPresenter, AddCardDetailsPath> implements AddCardDetailsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.card) BankCardWidget bankCardWidget;
   @InjectView(R.id.use_default_address_checkbox) CheckBox useDefaultAddressCheckBox;

   @InjectView(R.id.card_cvv) EditText cardCvv;
   @InjectView(R.id.address1) EditText address1;
   @InjectView(R.id.address2) EditText address2;
   @InjectView(R.id.city) EditText city;
   @InjectView(R.id.state) EditText state;
   @InjectView(R.id.zip) EditText zip;
   @InjectView(R.id.card_nickname) EditText cardNickname;

   @InjectView(R.id.set_default_address_checkBox) CheckBox setDefaultAddressCheckBox;
   @InjectView(R.id.set_default_card_checkBox) CheckBox setDefaultPaymentCard;

   @InjectView(R.id.default_address_text) TextView defaultAddressText;
   @InjectView(R.id.address_info) View addressInfoContainer;

   private DialogOperationScreen dialogOperationScreen;

   public AddCardDetailsScreen(Context context) {
      super(context);
   }

   public AddCardDetailsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public AddCardDetailsPresenter createPresenter() {
      return new AddCardDetailsPresenter(getContext(), getInjector(), getPath().getBankCard());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> navigateButtonClick());
   }

   @Override
   public void setCardBankInfo(BankCard bankCard) {
      bankCardWidget.setBankCardInfo(bankCard);
   }

   @Override
   public void hideDefaultAddressCheckbox() {
      useDefaultAddressCheckBox.setVisibility(GONE);
      showAddressFields();
   }

   @Override
   public void setDefaultAddress(AddressInfoWithLocale defaultAddressInfo) {
      defaultAddressText.setText(AddressUtil.obtainAddressLabel(defaultAddressInfo));
   }

   @Override
   public void setAsDefaultPaymentCard(boolean defaultPaymentCard) {
      setDefaultPaymentCard.setChecked(defaultPaymentCard);
   }

   @Override
   public void showDefaultAddress() {
      addressInfoContainer.setVisibility(GONE);
      defaultAddressText.setVisibility(VISIBLE);
   }

   @Override
   public void showAddressFields() {
      addressInfoContainer.setVisibility(VISIBLE);
      defaultAddressText.setVisibility(GONE);
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      if (dialogOperationScreen == null) dialogOperationScreen = new DialogOperationScreen(this);
      return dialogOperationScreen;
   }

   protected void navigateButtonClick() {
      presenter.goBack();
   }

   @OnCheckedChanged(R.id.use_default_address_checkbox)
   public void useDefaultAddressCheckedChanged(boolean isChecked) {
      presenter.useDefaultAddressRequirement(isChecked);
   }

   @OnClick(R.id.confirm_button)
   public void onConfirmButtonClicked() {
      String cvv = cardCvv.getText().toString().trim();
      String nickname = cardNickname.getText().toString().trim();
      boolean useDefaultAddress = useDefaultAddressCheckBox.isChecked();
      boolean setDefaultAddress = setDefaultAddressCheckBox.isChecked();

      AddressInfo addressInfo = ImmutableAddressInfo.builder()
            .address1(address1.getText().toString().trim())
            .address2(address2.getText().toString().trim())
            .city(city.getText().toString().trim())
            .state(state.getText().toString().trim())
            .zip(zip.getText().toString().trim())
            .build();

      getPresenter().onCardInfoConfirmed(addressInfo, cvv, nickname, useDefaultAddress, setDefaultAddress);
   }
}
