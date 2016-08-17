package com.worldventures.dreamtrips.modules.feed.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.Hashtag;

import java.util.ArrayList;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class MetaData implements Parcelable {

   ArrayList<Hashtag> hashtags;

   public MetaData() {
   }

   protected MetaData(Parcel in) {
      hashtags = in.createTypedArrayList(Hashtag.CREATOR);
   }

   public ArrayList<Hashtag> getHashtags() {
      return hashtags;
   }

   public static final Creator<MetaData> CREATOR = new Creator<MetaData>() {
      @Override
      public MetaData createFromParcel(Parcel in) {
         return new MetaData(in);
      }

      @Override
      public MetaData[] newArray(int size) {
         return new MetaData[size];
      }
   };

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeTypedList(hashtags);
   }
}
