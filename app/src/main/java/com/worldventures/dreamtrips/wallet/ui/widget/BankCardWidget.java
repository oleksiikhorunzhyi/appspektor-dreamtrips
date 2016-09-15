package com.worldventures.dreamtrips.wallet.ui.widget;


import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.util.BankCardHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BankCardWidget extends FrameLayout {

   @InjectView(R.id.defaultCardLabel) TextView defaultCardLabel;
   @InjectView(R.id.bankLabel) TextView tvBankLabel;
   @InjectView(R.id.cardTitle) TextView cardTitle;
   @InjectView(R.id.cardNumber) TextView cardNumber;
   @InjectView(R.id.typeIcon) ImageView cardTypeIcon;
   @InjectView(R.id.expireDate) TextView expireDate;
   @InjectView(R.id.cardType) TextView cardType;

   private View bankCardHolder;

   public BankCardWidget(Context context) {
      super(context);
   }

   public BankCardWidget(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public BankCardWidget(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      View.inflate(getContext(), R.layout.custom_view_bank_card, this);
      bankCardHolder = getChildAt(0);
      ButterKnife.inject(this);
   }

   public void setBankCardInfoForList(BankCardHelper bankCardHelper, BankCard bankCard) {
      setBankCardInfo(bankCardHelper.formattedBankNameWithCardNumber(bankCard), bankCardHelper, bankCard);
   }

   public void setBankCardInfo(BankCardHelper bankCardHelper, BankCard bankCard) {
      setBankCardInfo(bankCardHelper.formattedBankName(bankCard), bankCardHelper, bankCard);
   }

   private void setBankCardInfo(CharSequence bankLabel, BankCardHelper bankCardHelper, BankCard bankCard) {
      cardTitle.setText(bankCard.title());
      cardNumber.setText(String.format("•••• •••• •••• %04d", bankCard.number() % 10000));
      expireDate.setText(String.format("%02d/%02d", bankCard.expiryMonth(), bankCard.expiryYear()));
      tvBankLabel.setText(bankLabel);
      cardTypeIcon.setImageResource(bankCardHelper.obtainFinancialServiceImageRes(bankCard.issuerInfo().financialService()));
      cardType.setText(bankCardHelper.obtainCardType(bankCard.issuerInfo().cardType()));
   }

   public void setBankCardHolder(@DrawableRes int resource) {
      bankCardHolder.setBackgroundResource(resource);
   }

   public void setAsDefault(boolean isDefault) {
      defaultCardLabel.setVisibility(isDefault ? VISIBLE : GONE);
   }

}
