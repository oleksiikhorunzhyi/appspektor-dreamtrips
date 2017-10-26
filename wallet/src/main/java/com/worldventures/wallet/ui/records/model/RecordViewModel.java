package com.worldventures.wallet.ui.records.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.wallet.domain.entity.record.RecordType;

public class RecordViewModel implements Parcelable {

   private final String id;
   private final int cvvLength;
   private final String nickName;
   private final String ownerName;
   private final String cardNumber;
   private final String expireDate;
   private final RecordType recordType;

   public RecordViewModel(String id, int cvvLength, String nickName, String ownerName, String cardNumber, String expireDate, RecordType recordType) {
      this.id = id;
      this.cvvLength = cvvLength;
      this.nickName = nickName;
      this.ownerName = ownerName;
      this.cardNumber = cardNumber;
      this.expireDate = expireDate;
      this.recordType = recordType;
   }

   protected RecordViewModel(Parcel in) {
      id = in.readString();
      cvvLength = in.readInt();
      nickName = in.readString();
      ownerName = in.readString();
      cardNumber = in.readString();
      expireDate = in.readString();
      recordType = (RecordType) in.readSerializable();
   }

   public static final Creator<RecordViewModel> CREATOR = new Creator<RecordViewModel>() {
      @Override
      public RecordViewModel createFromParcel(Parcel in) {
         return new RecordViewModel(in);
      }

      @Override
      public RecordViewModel[] newArray(int size) {
         return new RecordViewModel[size];
      }
   };

   public String getId() {
      return id;
   }

   public int getCvvLength() {
      return cvvLength;
   }

   public String getNickName() {
      return nickName;
   }

   public String getOwnerName() {
      return ownerName;
   }

   public String getCardNumber() {
      return cardNumber;
   }

   public String getExpireDate() {
      return expireDate;
   }

   public RecordType getRecordType() {
      return recordType;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(id);
      dest.writeInt(cvvLength);
      dest.writeString(nickName);
      dest.writeString(ownerName);
      dest.writeString(cardNumber);
      dest.writeString(expireDate);
      dest.writeSerializable(recordType);
   }
}
