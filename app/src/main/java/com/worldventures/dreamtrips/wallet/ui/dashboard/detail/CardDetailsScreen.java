package com.worldventures.dreamtrips.wallet.ui.dashboard.detail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.dialog.ChangeDefaultPaymentCardDialog;
import com.worldventures.dreamtrips.wallet.ui.widget.BankCardWidget;
import com.worldventures.dreamtrips.wallet.util.AddressUtil;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.Observable;

public class CardDetailsScreen extends WalletFrameLayout<CardDetailsPresenter.Screen, CardDetailsPresenter, CardDetailsPath> implements CardDetailsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @InjectView(R.id.default_payment_card_checkbox) CheckBox defaultPaymentCardCheckBox;
   @InjectView(R.id.address_textview) TextView addressText;

   @InjectView(R.id.card) BankCardWidget bankCardWidget;

   private Observable<Boolean> setAsDefaultCardObservable;

   public CardDetailsScreen(Context context) {
      super(context);
   }

   public CardDetailsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public CardDetailsPresenter createPresenter() {
      return new CardDetailsPresenter(getContext(), getInjector(), getPath().getBankCard());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      toolbar.setNavigationOnClickListener(v -> navigateButtonClick());
      setAsDefaultCardObservable = RxCompoundButton.checkedChanges(defaultPaymentCardCheckBox).skip(1);
   }

   @OnClick(R.id.delete_button)
   public void onDeleteCardClicked() {
      getPresenter().onDeleteCardClick();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   public void setTitle(String title) {
      toolbar.setTitle(title);
   }

   @Override
   public void showCardBankInfo(BankCard bankCard) {
      bankCardWidget.setBankCardInfo(bankCard);
   }

   @Override
   public void showDefaultCardDialog(@NonNull String bankCardName) {
      new ChangeDefaultPaymentCardDialog(getContext(), bankCardName)
            .setOnConfirmAction(() -> getPresenter().defaultCardDialogConfirmed(true))
            .setOnCancelAction(() -> getPresenter().defaultCardDialogConfirmed(false))
            .show();
   }

   @Override
   public void showDeleteCardDialog() {
      Context context = getContext();
      SweetAlertDialog sweetDialog = new SweetAlertDialog(context)
            .setTitleText(context.getResources().getString(R.string.wallet_card_details_delete_card_dialog_title))
            .setContentText(context.getResources().getString(R.string.wallet_card_details_delete_card_dialog_content))
            .setConfirmClickListener(dialog -> {
               dialog.dismissWithAnimation();
               getPresenter().onDeleteCardConfirmed();
            });
      sweetDialog.show();
      sweetDialog.showCancelButton(true);
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

   protected void navigateButtonClick() {
      presenter.goBack();
   }
}
