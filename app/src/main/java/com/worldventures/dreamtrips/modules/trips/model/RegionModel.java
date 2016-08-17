package com.worldventures.dreamtrips.modules.trips.model;

import android.os.Parcel;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;


@DefaultSerializer(CompatibleFieldSerializer.class)
public class RegionModel extends BaseEntity {

   private String name;
   private boolean checked = true;

   public RegionModel() {
      super();
   }

   public boolean isChecked() {
      return checked;
   }

   public void setChecked(boolean checked) {
      this.checked = checked;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeString(this.name);
      dest.writeByte((byte) (this.checked ? 1 : 0));
   }

   public RegionModel(Parcel in) {
      super(in);
      this.name = in.readString();
      this.checked = in.readByte() != 0;
   }

   public static final Creator<RegionModel> CREATOR = new Creator<RegionModel>() {
      @Override
      public RegionModel createFromParcel(Parcel in) {
         return new RegionModel(in);
      }

      @Override
      public RegionModel[] newArray(int size) {
         return new RegionModel[size];
      }
   };
}
