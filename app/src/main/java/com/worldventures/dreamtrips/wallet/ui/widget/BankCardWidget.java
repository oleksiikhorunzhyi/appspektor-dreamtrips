package com.worldventures.dreamtrips.wallet.ui.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BankCardWidget extends FrameLayout {

   @InjectView(R.id.defaultCardLabel) TextView defaultCardLabel;
   @InjectView(R.id.bankLabel) TextView bankLabel;
   @InjectView(R.id.cardTitle) TextView cardTitle;
   @InjectView(R.id.cardNumber) TextView cardNumber;
   @InjectView(R.id.typeIcon) ImageView cardTypeIcon;
   @InjectView(R.id.expireDate) TextView expireDate;

   public BankCardWidget(Context context) {
      super(context);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      View.inflate(getContext(), R.layout.custom_view_bank_card, this);
      ButterKnife.inject(this);
   }

   public BankCardWidget(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public BankCardWidget(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   public void setBankCardInfo(BankCard bankCard) {
      cardTitle.setText(bankCard.title());
      cardNumber.setText(String.format("•••• •••• •••• •••• %04d", bankCard.number() % 10000));
      expireDate.setText(String.format("%02d/%02d", bankCard.expiryMonth(), bankCard.expiryYear()));
      //// TODO: add setting cardTypeIcon and bank name
   }

   public void setAsDefault(boolean isDefault) {
      defaultCardLabel.setVisibility(isDefault? VISIBLE : GONE);
   }

}
