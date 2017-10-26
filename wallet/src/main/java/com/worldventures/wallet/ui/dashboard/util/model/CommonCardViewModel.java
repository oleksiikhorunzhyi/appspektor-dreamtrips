package com.worldventures.wallet.ui.dashboard.util.model;

import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.view.View;

import com.worldventures.wallet.BR;
import com.worldventures.wallet.ui.common.adapter.BaseViewModel;
import com.worldventures.wallet.ui.dashboard.util.adapter.DashboardHolderTypeFactory;

public class CommonCardViewModel extends BaseViewModel<DashboardHolderTypeFactory> implements Parcelable {

   private final String recordId;
   private CharSequence cardName;
   private final StackType cardType;
   private final String cardTypeName;
   private boolean defaultCard;
   private final CharSequence cardLastDigitsShort;
   private final String cardHolderName;
   private final CharSequence cardLastDigitsLong;
   private final CharSequence goodThrough;
   private final @DrawableRes int cardBackGround;
   private final boolean sampleCard;

   public CommonCardViewModel(String recordId, CharSequence cardName, StackType cardType, String cardTypeName,
         boolean defaultCard, CharSequence cardLastDigitsShort, String cardHolderName,
         CharSequence cardLastDigitsLong, CharSequence goodThrough, @DrawableRes int cardBackGround, boolean sampleCard) {
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
      this.sampleCard = sampleCard;
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
      sampleCard = in.readByte() != 0;
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

   @Bindable
   public CharSequence getCardName() {
      return cardName;
   }

   public StackType getCardType() {
      return cardType;
   }

   public String getCardTypeName() {
      return cardTypeName;
   }

   @Bindable
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

   public void setCardName(CharSequence cardName) {
      this.cardName = cardName;
      notifyPropertyChanged(BR.cardName);

   }

   public void setDefaultCard(boolean defaultCard) {
      this.defaultCard = defaultCard;
      notifyPropertyChanged(BR.defaultCard);
   }

   @BindingAdapter({"cardBackground"})
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
      dest.writeByte((byte) (sampleCard ? 1 : 0));
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      CommonCardViewModel that = (CommonCardViewModel) o;

      if (defaultCard != that.defaultCard) return false;
      if (cardBackGround != that.cardBackGround) return false;
      if (sampleCard != that.sampleCard) return false;
      if (recordId != null ? !recordId.equals(that.recordId) : that.recordId != null) return false;
      if (cardName != null ? !cardName.equals(that.cardName) : that.cardName != null) return false;
      if (cardType != that.cardType) return false;
      if (cardTypeName != null ? !cardTypeName.equals(that.cardTypeName) : that.cardTypeName != null) return false;
      if (cardLastDigitsShort != null ? !cardLastDigitsShort.equals(that.cardLastDigitsShort) : that.cardLastDigitsShort != null)
         return false;
      if (cardHolderName != null ? !cardHolderName.equals(that.cardHolderName) : that.cardHolderName != null)
         return false;
      if (cardLastDigitsLong != null ? !cardLastDigitsLong.equals(that.cardLastDigitsLong) : that.cardLastDigitsLong != null)
         return false;
      return goodThrough != null ? goodThrough.equals(that.goodThrough) : that.goodThrough == null;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (recordId != null ? recordId.hashCode() : 0);
      result = 31 * result + (cardName != null ? cardName.hashCode() : 0);
      result = 31 * result + (cardType != null ? cardType.hashCode() : 0);
      result = 31 * result + (cardTypeName != null ? cardTypeName.hashCode() : 0);
      result = 31 * result + (defaultCard ? 1 : 0);
      result = 31 * result + (cardLastDigitsShort != null ? cardLastDigitsShort.hashCode() : 0);
      result = 31 * result + (cardHolderName != null ? cardHolderName.hashCode() : 0);
      result = 31 * result + (cardLastDigitsLong != null ? cardLastDigitsLong.hashCode() : 0);
      result = 31 * result + (goodThrough != null ? goodThrough.hashCode() : 0);
      result = 31 * result + cardBackGround;
      result = 31 * result + (sampleCard ? 1 : 0);
      return result;
   }
}
