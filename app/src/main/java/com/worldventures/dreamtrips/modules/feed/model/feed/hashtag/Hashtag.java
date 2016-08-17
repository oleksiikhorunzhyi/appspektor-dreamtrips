package com.worldventures.dreamtrips.modules.feed.model.feed.hashtag;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class Hashtag implements Parcelable, Serializable {

   String name;

   public Hashtag() {
   }

   public Hashtag(String name) {
      this.name = name;
   }

   protected Hashtag(Parcel in) {
      name = in.readString();
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public static final Creator<Hashtag> CREATOR = new Creator<Hashtag>() {
      @Override
      public Hashtag createFromParcel(Parcel in) {
         return new Hashtag(in);
      }

      @Override
      public Hashtag[] newArray(int size) {
         return new Hashtag[size];
      }
   };

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(name);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Hashtag hashtag1 = (Hashtag) o;

      return name != null ? name.equals(hashtag1.name) : hashtag1.name == null;
   }

   @Override
   public int hashCode() {
      return name != null ? name.hashCode() : 0;
   }

   @Override
   public String toString() {
      return "Hashtag{" +
            "name='" + name + '\'' +
            '}';
   }
}
