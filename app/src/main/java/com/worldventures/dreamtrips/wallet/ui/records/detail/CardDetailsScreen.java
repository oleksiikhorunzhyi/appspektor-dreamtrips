package com.worldventures.dreamtrips.wallet.ui.records.detail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.dialog.ChangeDefaultPaymentCardDialog;
import com.worldventures.dreamtrips.wallet.ui.widget.BankCardWidget;
import com.worldventures.dreamtrips.wallet.util.AddressUtil;
import com.worldventures.dreamtrips.wallet.util.BankCardHelper;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;

public class CardDetailsScreen extends WalletLinearLayout<CardDetailsPresenter.Screen, CardDetailsPresenter, CardDetailsPath>
      implements CardDetailsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.default_payment_card_checkbox) SwitchCompat defaultPaymentCardCheckBox;
   @InjectView(R.id.address_textview) TextView addressText;
   @InjectView(R.id.card_name) EditText cardNickname;
   @InjectView(R.id.card) BankCardWidget bankCardWidget;

   private Observable<Boolean> setAsDefaultCardObservable;
   private MaterialDialog connectedErrorDialog;
   private final BankCardHelper bankCardHelper;

   public CardDetailsScreen(Context context) {
      this(context, null);
   }

   public CardDetailsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
      bankCardHelper = new BankCardHelper(context);
   }

   @NonNull
   @Override
   public CardDetailsPresenter createPresenter() {
      return new CardDetailsPresenter(getContext(), getInjector(), getPath().getBankCard());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();

      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> presenter.goBack());
      setAsDefaultCardObservable = RxCompoundButton.checkedChanges(defaultPaymentCardCheckBox).skip(1);
   }

   @OnClick(R.id.delete_button)
   public void onDeleteCardClicked() {
      getPresenter().onDeleteCardClick();
   }

   @OnClick(R.id.edit_billing_address)
   public void onEditBillingAddress() {
      getPresenter().editAddress();
   }

   @OnClick(R.id.pay_this_card_button)
   public void onPayThisCardClicked() {
      getPresenter().payThisCard();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   public void showCardBank(BankCard bankCard) {
      // // TODO: 12/6/16 remove bank name
      toolbar.setTitle(bankCardHelper.financialServiceWithCardNumber(bankCard));
      bankCardWidget.setBankCard(bankCard);
      cardNickname.setText(bankCard.nickName());
   }

   @Override
   public void showDefaultCardDialog(BankCard defaultBankCard) {
      // TODO: 12/6/16 remove bank name
      new ChangeDefaultPaymentCardDialog(getContext(), bankCardHelper.bankNameWithCardNumber(defaultBankCard))
            .setOnConfirmAction(() -> getPresenter().defaultCardDialogConfirmed(true))
            .setOnCancelAction(() -> getPresenter().defaultCardDialogConfirmed(false))
            .show();
   }

   @Override
   public void showDeleteCardDialog() {
      new MaterialDialog.Builder(getContext())
            .title(R.string.wallet_card_details_delete_card_dialog_title)
            .content(R.string.wallet_card_details_delete_card_dialog_content)
            .positiveText(R.string.ok)
            .negativeText(R.string.cancel)
            .onPositive((dialog, which) -> getPresenter().onDeleteCardConfirmed())
            .build()
            .show();
   }

   @Override
   public void showConnectionErrorDialog() {
      if (connectedErrorDialog == null) {
         connectedErrorDialog = new MaterialDialog.Builder(getContext())
               .title(R.string.wallet_smartcard_disconnected_label)
               .content(R.string.wallet_smartcard_connection_try_description)
               .positiveText(R.string.ok)
               .onPositive((dialog, which) -> {
                  dialog.cancel();
                  connectedErrorDialog = null;
               })
               .dismissListener((dialog) -> connectedErrorDialog = null)
               .build();
         connectedErrorDialog.show();
      }
   }

   @Override
   public void showCardIsReadyDialog(String cardName) {
      MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext());
      builder.content(getString(R.string.wallet_wizard_card_list_card_is_ready_text, cardName))
            .positiveText(R.string.ok)
            .onPositive((dialog, which) -> getPresenter().onCardIsReadyDialogShown())
            .build()
            .show();
   }

   @Override
   public Observable<Boolean> setAsDefaultPaymentCardCondition() {
      return setAsDefaultCardObservable;
   }

   @Override
   public void setDefaultCardCondition(boolean defaultCard) {
      defaultPaymentCardCheckBox.setChecked(defaultCard);
   }

   @Override
   public void showDefaultAddress(AddressInfoWithLocale addressInfoWithLocale) {
      addressText.setText(AddressUtil.obtainAddressLabel(addressInfoWithLocale));
   }

   @Override
   public String getUpdateNickname() {
      return cardNickname.getText().toString();
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }
}
