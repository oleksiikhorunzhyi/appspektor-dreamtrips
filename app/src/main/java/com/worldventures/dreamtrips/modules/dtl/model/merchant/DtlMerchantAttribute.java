package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import java.util.Comparator;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlMerchantAttribute implements Parcelable, Comparable<DtlMerchantAttribute> {

   private String name;

   public DtlMerchantAttribute() {
   }

   public DtlMerchantAttribute(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable
   ///////////////////////////////////////////////////////////////////////////

   protected DtlMerchantAttribute(Parcel in) {
      name = in.readString();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(name);
   }

   @Override
   public int describeContents() {
      return 0;
   }

   public static final Creator<DtlMerchantAttribute> CREATOR = new Creator<DtlMerchantAttribute>() {
      @Override
      public DtlMerchantAttribute createFromParcel(Parcel in) {
         return new DtlMerchantAttribute(in);
      }

      @Override
      public DtlMerchantAttribute[] newArray(int size) {
         return new DtlMerchantAttribute[size];
      }
   };

   ///////////////////////////////////////////////////////////////////////////
   // java.lang.Object-overridden, sorting
   ///////////////////////////////////////////////////////////////////////////

   public static final Comparator<DtlMerchantAttribute> NAME_ALPHABETIC_COMPARATOR = new Comparator<DtlMerchantAttribute>() {
      @Override
      public int compare(DtlMerchantAttribute lhs, DtlMerchantAttribute rhs) {
         return lhs.name.compareToIgnoreCase(rhs.name);
      }
   };

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      //
      DtlMerchantAttribute that = (DtlMerchantAttribute) o;
      //
      return !(name != null ? !name.equals(that.name) : that.name != null);

   }

   @Override
   public int hashCode() {
      return name != null ? name.hashCode() : 0;
   }

   @Override
   public String toString() {
      return name;
   }

   @Override
   public int compareTo(DtlMerchantAttribute another) {
      return name.compareToIgnoreCase(another.name);
   }
}
