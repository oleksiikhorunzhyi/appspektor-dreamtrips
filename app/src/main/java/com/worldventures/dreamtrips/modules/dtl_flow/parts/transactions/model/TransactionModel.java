package com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class TransactionModel implements Parcelable {
   private String id;
   private String merchantName;
   private boolean rewardStatus;
   private String receiptUrl;
   private double subTotalAmount;
   private double totalAmount;
   private double tax;
   private double tip;
   private int earnedPoints;
   private Date transactionDate;

   public TransactionModel() { }

   public void setId(String id) {
      this.id = id;
   }

   public String getId() {
      return id;
   }

   public String getMerchantName() {
      return merchantName;
   }

   public void setMerchantName(String merchantName) {
      this.merchantName = merchantName;
   }

   public boolean isRewardStatus() {
      return rewardStatus;
   }

   public void setRewardStatus(boolean rewardStatus) {
      this.rewardStatus = rewardStatus;
   }

   public String getReceiptUrl() {
      return receiptUrl;
   }

   public void setReceiptUrl(String receiptUrl) {
      this.receiptUrl = receiptUrl;
   }

   public double getSubTotalAmount() {
      return subTotalAmount;
   }

   public void setSubTotalAmount(double subTotalAmount) {
      this.subTotalAmount = subTotalAmount;
   }

   public double getTotalAmount() {
      return totalAmount;
   }

   public void setTotalAmount(double totalAmount) {
      this.totalAmount = totalAmount;
   }

   public double getTax() {
      return tax;
   }

   public void setTax(double tax) {
      this.tax = tax;
   }

   public double getTip() {
      return tip;
   }

   public void setTip(double tip) {
      this.tip = tip;
   }

   public int getEarnedPoints() {
      return earnedPoints;
   }

   public void setEarnedPoints(int earnedPoints) {
      this.earnedPoints = earnedPoints;
   }

   public Date getTransactionDate() {
      return transactionDate;
   }

   public void setTransactionDate(Date transactionDate) {
      this.transactionDate = transactionDate;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TransactionModel that = (TransactionModel) o;

      return id != null ? id.equals(that.id) : that.id == null;
   }

   @Override
   public int hashCode() {
      return id != null ? id.hashCode() : 0;
   }

   protected TransactionModel(Parcel in) {
      id = in.readString();
      merchantName = in.readString();
      transactionDate = (Date) in.readSerializable();
      rewardStatus = in.readByte() != 0;
      receiptUrl = in.readString();
      subTotalAmount = in.readDouble();
      totalAmount = in.readDouble();
      tax = in.readDouble();
      tip = in.readDouble();
      earnedPoints = in.readInt();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(id);
      dest.writeString(merchantName);
      dest.writeSerializable(transactionDate);
      dest.writeByte((byte) (rewardStatus ? 1 : 0));
      dest.writeString(receiptUrl);
      dest.writeDouble(subTotalAmount);
      dest.writeDouble(totalAmount);
      dest.writeDouble(tax);
      dest.writeDouble(tip);
      dest.writeInt(earnedPoints);
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