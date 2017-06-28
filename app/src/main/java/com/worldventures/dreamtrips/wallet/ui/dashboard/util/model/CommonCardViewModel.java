package com.worldventures.dreamtrips.wallet.ui.dashboard.util.model;


import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.view.View;

import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.DashboardHolderTypeFactory;

public class CommonCardViewModel extends BaseViewModel<DashboardHolderTypeFactory> implements Parcelable {

   private String recordId;
   private CharSequence cardName;
   private StackType cardType;
   private String cardTypeName;
   private boolean defaultCard;
   private CharSequence cardLastDigitsShort;
   private String cardHolderName;
   private CharSequence cardLastDigitsLong;
   private CharSequence goodThrough;
   private @DrawableRes int cardBackGround;
   private boolean sampleCard = false;

   public CommonCardViewModel(String recordId, CharSequence cardName, StackType cardType, String cardTypeName,
         boolean defaultCard, CharSequence cardLastDigitsShort, String cardHolderName,
         CharSequence cardLastDigitsLong, CharSequence goodThrough, @DrawableRes int cardBackGround) {
      this.recordId = recordId;
      this.cardName = cardName;
      this.cardType = cardType;
      this.cardTypeName = cardTypeName;
      this.defaultCard = defaultCard;
      this.cardLastDigitsShort = cardLastDigitsShort;
      this.cardHolderName = cardHolderName;
      this.cardLastDigitsLong = cardLastDigitsLong;
      this.goodThrough = goodThrough;
      this.cardBackGround = cardBackGround;
      modelId = recordId + cardTypeName + cardName;
   }


   protected CommonCardViewModel(Parcel in) {
      recordId = in.readString();
      cardName = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
      cardType = StackType.valueOf(in.readString());
      cardTypeName = in.readString();
      defaultCard = in.readByte() != 0;
      cardLastDigitsShort = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
      cardHolderName = in.readString();
      cardLastDigitsLong = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
      goodThrough = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
      cardBackGround = in.readInt();
      modelId = recordId + cardTypeName + cardName;
   }

   public static final Creator<CommonCardViewModel> CREATOR = new Creator<CommonCardViewModel>() {
      @Override
      public CommonCardViewModel createFromParcel(Parcel in) {
         return new CommonCardViewModel(in);
      }

      @Override
      public CommonCardViewModel[] newArray(int size) {
         return new CommonCardViewModel[size];
      }
   };

   public String getRecordId() {
      return recordId;
   }

   public CharSequence getCardName() {
      return cardName;
   }

   public StackType getCardType() {
      return cardType;
   }

   public String getCardTypeName() {
      return cardTypeName;
   }

   public boolean isDefaultCard() {
      return defaultCard;
   }

   public CharSequence getCardLastDigitsShort() {
      return cardLastDigitsShort;
   }

   public String getCardHolderName() {
      return cardHolderName;
   }

   public CharSequence getCardLastDigitsLong() {
      return cardLastDigitsLong;
   }

   public CharSequence getGoodThrough() {
      return goodThrough;
   }

   public int getCardBackGround() {
      return cardBackGround;
   }

   public boolean isSampleCard() {
      return sampleCard;
   }

   @BindingAdapter({"bind:cardBackground"})
   public static void getCardBackground(View view, @DrawableRes int backGround) {
      view.setBackgroundResource(backGround);
   }

   @Override
   public int type(DashboardHolderTypeFactory typeFactory) {
      return typeFactory.type(this);
   }

   public enum StackType {
      PAYMENT, LOYALTY
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(recordId);
      TextUtils.writeToParcel(cardName, dest, 0);
      dest.writeString((cardType == null) ? "" : cardType.name());
      dest.writeString(cardTypeName);
      dest.writeByte((byte) (defaultCard ? 1 : 0));
      TextUtils.writeToParcel(cardLastDigitsShort, dest, 0);
      dest.writeString(cardHolderName);
      TextUtils.writeToParcel(cardLastDigitsLong, dest, 0);
      TextUtils.writeToParcel(goodThrough, dest, 0);
      dest.writeInt(cardBackGround);
   }
}
