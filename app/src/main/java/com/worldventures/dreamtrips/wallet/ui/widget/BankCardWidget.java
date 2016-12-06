package com.worldventures.dreamtrips.wallet.ui.widget;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.util.BankCardHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BankCardWidget extends FrameLayout {

   @InjectView(R.id.tv_card_type) TextView tvCardType;
   @InjectView(R.id.tv_card_name) TextView tvCardName;
   @InjectView(R.id.tv_owner_name) TextView tvOwnerName;
   @InjectView(R.id.tv_card_number) TextView tvCardNumber;
   @InjectView(R.id.tv_expire_date) TextView tvExpireDate;
   @InjectView(R.id.wallet_sample_holder) View sampleCardHolder;
   @InjectView(R.id.tv_default_card_label) TextView tvDefaultCardLabel;
   @InjectView(R.id.tv_short_card_number) TextView tvShortCardNumber;

   private View bankCardHolder;

   private final BankCardHelper bankCardHelper;
   private final SpannableString goodThru;

   private boolean showShortNumber;

   public BankCardWidget(Context context) {
      this(context, null);
   }

   public BankCardWidget(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public BankCardWidget(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      bankCardHelper = new BankCardHelper(context);
      goodThru = new SpannableString(getResources().getString(R.string.wallet_bank_card_good_thru));
      goodThru.setSpan(new RelativeSizeSpan(.65f), 0, goodThru.length(), 0);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      View.inflate(getContext(), R.layout.custom_view_bank_card, this);
      bankCardHolder = getChildAt(0);
      ButterKnife.inject(this);
      tvShortCardNumber.setVisibility(GONE);
   }

   public void setShowShortNumber(boolean show) {
      this.showShortNumber = show;
      tvShortCardNumber.setVisibility(show ? VISIBLE : GONE);
   }

   public void setBankCard(BankCard card) {
      setCardName(card.nickName());
      setOwnerName(card.cardNameHolder());
      setCardNumber(card.number());
      setExpireDate(card.expDate());
      setCardType(card.issuerInfo().cardType());
      setCardCategory(card.category());
   }

   //   properties:
   public void setCardName(CharSequence cardName) {
      tvCardName.setText(bankCardHelper.toBoldSpannable(cardName));
   }

   public void setOwnerName(CharSequence ownerName) {
      tvOwnerName.setText(ownerName);
   }

   public void setBankCardHolder(@DrawableRes int resource) {
      bankCardHolder.setBackgroundResource(resource);
   }

   public void setAsDefault(boolean isDefault) {
      tvDefaultCardLabel.setVisibility(isDefault ? VISIBLE : INVISIBLE);
   }

   public void setExpireDate(CharSequence expireDate) {
      tvExpireDate.setText(new SpannableStringBuilder()
            .append(goodThru)
            .append(" ")
            .append(expireDate)
      );
   }

   public void setCardNumber(long cardNumber) {
      tvCardNumber.setText(bankCardHelper.obtainFullCardNumber(cardNumber));
      if (showShortNumber) {
         tvShortCardNumber.setText(bankCardHelper.obtainShortCardNumber(cardNumber));
      }
   }

   public void setCardCategory(Card.Category cardCategory) {
      if (cardCategory == Card.Category.SAMPLE) {
         sampleCardHolder.setVisibility(VISIBLE);
      } else {
         sampleCardHolder.setVisibility(GONE);
      }
   }

   public void setCardType(BankCard.CardType cardType) {
      tvCardType.setText(bankCardHelper.obtainCardType(cardType));
   }
}
