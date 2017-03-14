package com.worldventures.dreamtrips.wallet.ui.records.detail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.command.UpdateRecordCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.success.SimpleToastSuccessView;
import com.worldventures.dreamtrips.wallet.ui.dialog.ChangeDefaultPaymentCardDialog;
import com.worldventures.dreamtrips.wallet.ui.widget.BankCardWidget;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletSwitcher;
import com.worldventures.dreamtrips.wallet.util.AddressUtil;
import com.worldventures.dreamtrips.wallet.util.WalletRecordUtil;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

import static com.worldventures.dreamtrips.wallet.util.WalletCardNameUtil.bindSpannableStringToTarget;

public class CardDetailsScreen extends WalletLinearLayout<CardDetailsPresenter.Screen, CardDetailsPresenter, CardDetailsPath>
      implements CardDetailsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.card) BankCardWidget bankCardWidget;

   @InjectView(R.id.address_textview) TextView tvAddress;
   @InjectView(R.id.card_name) EditText etCardNickname;
   @InjectView(R.id.card_nickname_label) TextView cardNicknameLabel;
   @InjectView(R.id.default_payment_card_checkbox) WalletSwitcher defaultPaymentCardSwitcher;
   @InjectView(R.id.cardNameInputLayout) TextInputLayout cardNameInputLayout;

   private Observable<Boolean> setAsDefaultCardObservable;
   private Observable<String> cardNicknameObservable;
   private MaterialDialog connectedErrorDialog;
   private final WalletRecordUtil walletRecordUtil;
   private MaterialDialog saveCardDataProgressDialog = null;

   public CardDetailsScreen(Context context) {
      this(context, null);
   }

   public CardDetailsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
      walletRecordUtil = new WalletRecordUtil(context);
   }

   @NonNull
   @Override
   public CardDetailsPresenter createPresenter() {
      return new CardDetailsPresenter(getContext(), getInjector(), getPath().getRecord());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();

      if (isInEditMode()) return;
      setupToolbar();

      setAsDefaultCardObservable = RxCompoundButton.checkedChanges(defaultPaymentCardSwitcher).skip(1);
      cardNicknameObservable = RxTextView.afterTextChangeEvents(etCardNickname).map(event -> event.editable()
            .toString()).skip(1);

      bindSpannableStringToTarget(cardNicknameLabel, R.string.wallet_card_details_label_card_nickname,
            R.string.wallet_add_card_details_hint_card_name_length, true, false);
   }

   private void setupToolbar() {
      toolbar.setNavigationOnClickListener(v -> presenter.goBack());
      toolbar.inflateMenu(R.menu.menu_wallet_payment_card_detail);
      toolbar.setOnMenuItemClickListener(item -> {
         switch (item.getItemId()) {
            case R.id.action_save:
               presenter.updateNickNameIfIsEdited();
            default:
               return false;
         }
      });
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
   public void showWalletRecord(Record record) {
      toolbar.setTitle(walletRecordUtil.financialServiceWithCardNumber(record));
      bankCardWidget.setBankCard(record);

      final String nickName = record.nickName();
      etCardNickname.setText(nickName);
      etCardNickname.setSelection(nickName.length());
   }

   @Override
   public void showDefaultCardDialog(Record defaultRecord) {
      new ChangeDefaultPaymentCardDialog(getContext(), walletRecordUtil.bankNameWithCardNumber(defaultRecord))
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
   public void setCardNickname(String cardNickname) {
      bankCardWidget.setCardName(cardNickname);
   }

   @Override
   public Observable<Boolean> setAsDefaultPaymentCardCondition() {
      return setAsDefaultCardObservable;
   }

   @Override
   public Observable<String> getCardNicknameObservable() {
      return cardNicknameObservable;
   }

   @Override
   public void setDefaultCardCondition(boolean defaultCard) {
      defaultPaymentCardSwitcher.setCheckedWithoutNotify(defaultCard);
   }

   @Override
   public void showDefaultAddress(AddressInfoWithLocale addressInfoWithLocale) {
      tvAddress.setText(AddressUtil.obtainAddressLabel(addressInfoWithLocale));
   }

   @Override
   public String getUpdateNickname() {
      return etCardNickname.getText().toString().trim();
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @Override
   public void showSCNonConnectionDialog() {
      new MaterialDialog.Builder(getContext())
            .title(R.string.wallet_card_settings_cant_connected)
            .content(R.string.wallet_card_settings_message_cant_connected)
            .positiveText(R.string.ok)
            .build()
            .show();
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
   public OperationView<UpdateRecordCommand> provideOperationSaveCardData() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_card_details_progress_save, false),
            new SimpleToastSuccessView<>(getContext(), R.string.wallet_card_details_success_save)
      );
   }

   @Override
   public void notifyCardDataIsSaved() {
      Toast.makeText(getContext(), R.string.wallet_card_details_success_save, Toast.LENGTH_SHORT).show();
   }

   @Override
   protected void onDetachedFromWindow() {
      if (saveCardDataProgressDialog != null) saveCardDataProgressDialog.dismiss();
      super.onDetachedFromWindow();
   }
}
