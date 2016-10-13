package com.worldventures.dreamtrips.modules.membership.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class SentInvite implements Parcelable {
   private String contact;
   private Date date;

   public SentInvite(String contact, Date date) {
      this.contact = contact;
      this.date = date;
   }

   public String getContact() {
      return contact;
   }

   public Date getDate() {
      return date;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.contact);
      dest.writeSerializable(this.date);
   }

   public SentInvite(Parcel in) {
      this.contact = in.readString();
      this.date = (Date) in.readSerializable();
   }

   public static final Creator<SentInvite> CREATOR = new Creator<SentInvite>() {
      @Override
      public SentInvite createFromParcel(Parcel in) {
         return new SentInvite(in);
      }

      @Override
      public SentInvite[] newArray(int size) {
         return new SentInvite[size];
      }
   };
}
