package com.worldventures.wallet.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.wallet.R;
import com.worldventures.wallet.domain.entity.record.RecordType;
import com.worldventures.wallet.ui.records.model.RecordViewModel;
import com.worldventures.wallet.util.WalletRecordUtil;

import java.util.Arrays;
import java.util.List;

public class BankCardWidget extends FrameLayout {

   private final WalletRecordUtil walletRecordUtil;
   private final SpannableString goodThru;

   private TextView tvCardType;
   private TextView tvCardName;
   private TextView tvOwnerName;
   private TextView tvCardNumber;
   private TextView tvExpireDate;
   private TextView tvDefaultCardLabel;
   private TextView tvShortCardNumber;
   private ImageView ivDefaultCardMarker;
   private View bankCardHolder;
   private List<TextView> textViews;
   private boolean showShortNumber;
   private int drawableResId;

   public BankCardWidget(Context context) {
      this(context, null);
   }

   public BankCardWidget(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public BankCardWidget(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      TypedArray a = context.getTheme().obtainStyledAttributes(
            attrs,
            R.styleable.BankCardWidget,
            0, 0);
      try {
         int attrId = a.getInt(R.styleable.BankCardWidget_cardDrawable, BankCardResource.BLUE.getAttrId());
         drawableResId = BankCardResource.fromAttrId(attrId).getDrawableResId();
      } finally {
         a.recycle();
      }
      walletRecordUtil = new WalletRecordUtil();
      goodThru = new SpannableString(getResources().getString(R.string.wallet_bank_card_good_thru));
      goodThru.setSpan(new RelativeSizeSpan(.65f), 0, goodThru.length(), 0);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      final View view = View.inflate(getContext(), R.layout.wallet_custom_view_record, this);
      tvCardType = view.findViewById(R.id.tv_card_type);
      tvCardName = view.findViewById(R.id.tv_card_name);
      tvOwnerName = view.findViewById(R.id.tv_owner_name);
      tvCardNumber = view.findViewById(R.id.tv_card_number);
      tvExpireDate = view.findViewById(R.id.tv_expire_date);
      tvDefaultCardLabel = view.findViewById(R.id.tv_default_card_label);
      tvShortCardNumber = view.findViewById(R.id.tv_short_card_number);
      tvShortCardNumber.setVisibility(GONE);
      ivDefaultCardMarker = view.findViewById(R.id.iv_default_card_marker);
      bankCardHolder = getChildAt(0);
      setBankCardHolder(drawableResId);
      textViews = Arrays.asList(tvCardType, tvCardName, tvOwnerName, tvCardNumber, tvExpireDate, tvDefaultCardLabel);
   }

   public void setUpCardAppearance(@DrawableRes int backgroundResId, boolean isCardDefault) {
      bankCardHolder.setBackground(ContextCompat.getDrawable(getContext(), backgroundResId));
      for (TextView textView : textViews) {
         textView.setTextColor(ContextCompat.getColor(textView.getContext(), android.R.color.white));
      }
      setAsDefault(isCardDefault);
   }

   public void setShowShortNumber(boolean show) {
      this.showShortNumber = show;
      tvShortCardNumber.setVisibility(show ? VISIBLE : GONE);
   }

   public void setBankCard(RecordViewModel recordViewModel) {
      setCardName(recordViewModel.getNickName());
      setOwnerName(recordViewModel.getOwnerName());
      setCardNumber(recordViewModel.getCardNumber());
      setExpireDate(recordViewModel.getExpireDate());
      setRecordType(recordViewModel.getRecordType());
   }

   //   properties:
   public void setCardName(CharSequence cardName) {
      tvCardName.setText(walletRecordUtil.toBoldSpannable(cardName));
   }

   public void setOwnerName(CharSequence ownerName) {
      tvOwnerName.setText(ownerName);
   }

   public void setBankCardHolder(@DrawableRes int resource) {
      bankCardHolder.setBackgroundResource(resource);
   }

   public void setAsDefault(boolean isDefault) {
      tvDefaultCardLabel.setVisibility(isDefault ? VISIBLE : INVISIBLE);
      ivDefaultCardMarker.setVisibility(isDefault ? VISIBLE : INVISIBLE);
   }

   public void setExpireDate(CharSequence expireDate) {
      tvExpireDate.setText(new SpannableStringBuilder()
            .append(goodThru)
            .append(" ")
            .append(expireDate)
      );
   }

   public void setCardNumber(String numberLastFourDigits) {
      tvCardNumber.setText(walletRecordUtil.obtainFullCardNumber(numberLastFourDigits));
      if (showShortNumber) {
         tvShortCardNumber.setText(walletRecordUtil.obtainShortCardNumber(numberLastFourDigits));
      }
   }

   public void setRecordType(RecordType recordType) {
      tvCardType.setText(walletRecordUtil.obtainRecordType(getContext(), recordType));
   }

   private enum BankCardResource {
      BLUE(0, R.drawable.wallet_card_blue_background),
      DARK_BLUE(2, R.drawable.wallet_card_dark_blue_background);

      private int attrId;
      private int drawableResId;

      BankCardResource(int attrId, int drawableResId) {
         this.attrId = attrId;
         this.drawableResId = drawableResId;
      }

      public int getAttrId() {
         return attrId;
      }

      public int getDrawableResId() {
         return drawableResId;
      }

      static BankCardResource fromAttrId(int attrId) {
         for (BankCardResource drawables : values()) {
            if (drawables.attrId == attrId) {
               return drawables;
            }
         }
         throw new IllegalArgumentException();
      }
   }
}
