package com.worldventures.dreamtrips.modules.trips.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class DateFilterItem implements Parcelable, Serializable {

   private Date startDate;
   private Date endDate;

   public DateFilterItem() {
      reset();
   }

   public void reset() {
      Calendar calendar = Calendar.getInstance();
      setStartDate(calendar.getTime());
      calendar.add(Calendar.MONTH, 12);
      setEndDate(calendar.getTime());
   }

   public void setStartDate(Date startDate) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(startDate);
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);

      this.startDate = calendar.getTime();
   }

   public void setEndDate(Date endDate) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(endDate);
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);

      this.endDate = calendar.getTime();
   }

   public Date getStartDate() {
      return startDate;
   }

   public Date getEndDate() {
      return endDate;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeLong(startDate != null ? startDate.getTime() : -1);
      dest.writeLong(endDate != null ? endDate.getTime() : -1);
   }

   protected DateFilterItem(Parcel in) {
      long tmpStartDate = in.readLong();
      this.startDate = tmpStartDate == -1 ? null : new Date(tmpStartDate);
      long tmpEndDate = in.readLong();
      this.endDate = tmpEndDate == -1 ? null : new Date(tmpEndDate);
   }

   public static final Creator<DateFilterItem> CREATOR = new Creator<DateFilterItem>() {
      public DateFilterItem createFromParcel(Parcel source) {
         return new DateFilterItem(source);
      }

      public DateFilterItem[] newArray(int size) {
         return new DateFilterItem[size];
      }
   };
}
