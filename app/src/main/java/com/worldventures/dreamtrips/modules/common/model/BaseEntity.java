package com.worldventures.dreamtrips.modules.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class BaseEntity implements Serializable, Parcelable {

   public BaseEntity() {
   }

   @TaggedFieldSerializer.Tag(0) protected int id;

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }
      BaseEntity that = (BaseEntity) o;
      return id == that.id;
   }

   @Override
   public int hashCode() {
      return id;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(this.id);
   }

   protected BaseEntity(Parcel in) {
      this.id = in.readInt();
   }

   public static final Creator<BaseEntity> CREATOR = new Creator<BaseEntity>() {
      @Override
      public BaseEntity createFromParcel(Parcel in) {
         return new BaseEntity(in);
      }

      @Override
      public BaseEntity[] newArray(int size) {
         return new BaseEntity[size];
      }
   };

}
