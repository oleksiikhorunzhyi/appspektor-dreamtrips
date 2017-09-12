package com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TransactionModel implements Parcelable {

   private String merchantName;
   private String subTotalAmount;
   private String earnedPoints;
   private String transactionDate;
   private boolean transactionSuccess;

   public TransactionModel() {

   }

   public String getMerchantName() {
      return merchantName;
   }

   public void setMerchantName(String merchantName) {
      this.merchantName = merchantName;
   }

   public String getSubTotalAmount() {
      return subTotalAmount;
   }

   public void setSubTotalAmount(String subTotalAmount) {
      this.subTotalAmount = subTotalAmount;
   }

   public String getEarnedPoints() {
      return earnedPoints;  }

   public void setEarnedPoints(String earnedPoints) {
      this.earnedPoints = earnedPoints;
   }

   public String getTransactionDate() {
      return transactionDate;
   }

   public void setTransactionDate(String transactionDate) {
      this.transactionDate = transactionDate;
   }

   public boolean isTransactionSuccess() {
      return transactionSuccess;
   }

   public void setTransactionSuccess(boolean transactionSuccess) {
      this.transactionSuccess = transactionSuccess;
   }

   protected TransactionModel(Parcel in) {
      merchantName = in.readString();
      subTotalAmount = in.readString();
      earnedPoints = in.readString();
      transactionDate = in.readString();
      transactionSuccess = in.readByte() != 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(merchantName);
      dest.writeString(subTotalAmount);
      dest.writeString(earnedPoints);
      dest.writeString(transactionDate);
      dest.writeByte((byte) (transactionSuccess ? 1 : 0));
   }

   @Override
   public int describeContents() {
      return 0;
   }

   public static final Creator<TransactionModel> CREATOR = new Creator<TransactionModel>() {
      @Override
      public TransactionModel createFromParcel(Parcel in) {
         return new TransactionModel(in);
      }

      @Override
      public TransactionModel[] newArray(int size) {
         return new TransactionModel[size];
      }
   };
}