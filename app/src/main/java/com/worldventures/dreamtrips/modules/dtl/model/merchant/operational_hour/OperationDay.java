package com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.innahema.collections.query.queriables.Queryable;

import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class OperationDay implements Parcelable {

   DayOfWeek dayOfWeek;
   List<OperationHours> operationHours;

   public OperationDay() {
   }

   public OperationDay(com.worldventures.dreamtrips.api.dtl.merchants.model.OperationDay operationDay) {
      dayOfWeek = DayOfWeek.from(operationDay.dayOfWeek());
      operationHours = Queryable.from(operationDay.operationHours()).map(OperationHours::new).toList();
   }

   public boolean isHaveOperationHours() {
      return operationHours != null && !operationHours.isEmpty();
   }

   public DayOfWeek getDayOfWeek() {
      return dayOfWeek;
   }

   public List<OperationHours> getOperationHours() {
      return operationHours;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      OperationDay that = (OperationDay) o;

      return dayOfWeek == that.dayOfWeek;
   }

   @Override
   public int hashCode() {
      return dayOfWeek != null ? dayOfWeek.hashCode() : 0;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable
   ///////////////////////////////////////////////////////////////////////////

   protected OperationDay(Parcel in) {
      dayOfWeek = (DayOfWeek) in.readSerializable();
      operationHours = in.createTypedArrayList(OperationHours.CREATOR);
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeSerializable(dayOfWeek);
      dest.writeTypedList(operationHours);
   }

   @Override
   public int describeContents() {
      return 0;
   }

   public static final Creator<OperationDay> CREATOR = new Creator<OperationDay>() {
      @Override
      public OperationDay createFromParcel(Parcel in) {
         return new OperationDay(in);
      }

      @Override
      public OperationDay[] newArray(int size) {
         return new OperationDay[size];
      }
   };
}
