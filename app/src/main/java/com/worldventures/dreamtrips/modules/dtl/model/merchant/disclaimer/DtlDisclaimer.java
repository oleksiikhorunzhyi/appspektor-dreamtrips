package com.worldventures.dreamtrips.modules.dtl.model.merchant.disclaimer;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.api.dtl.merchants.model.Disclaimer;
import com.worldventures.dreamtrips.api.dtl.merchants.model.DisclaimerType;

@SuppressWarnings("unused")
@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlDisclaimer implements Parcelable {

   private DisclaimerType type;
   private String text;

   public DtlDisclaimer() {
   }

   public DtlDisclaimer(Disclaimer disclaimer) {
      type = disclaimer.type();
      text = disclaimer.text();
   }

   public DisclaimerType getType() {
      return type;
   }

   public String getText() {
      return text;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable
   ///////////////////////////////////////////////////////////////////////////

   protected DtlDisclaimer(Parcel in) {
      type = (DisclaimerType) in.readSerializable();
      text = in.readString();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeSerializable(type);
      dest.writeString(text);
   }

   @Override
   public int describeContents() {
      return 0;
   }

   public static final Creator<DtlDisclaimer> CREATOR = new Creator<DtlDisclaimer>() {
      @Override
      public DtlDisclaimer createFromParcel(Parcel in) {
         return new DtlDisclaimer(in);
      }

      @Override
      public DtlDisclaimer[] newArray(int size) {
         return new DtlDisclaimer[size];
      }
   };

   @Override
   public String toString() {
      return text;
   }
}
