package com.worldventures.dreamtrips.modules.feed.model.feed.item;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class Links implements Serializable, Parcelable {

   private List<User> users;

   public List<User> getUsers() {
      return users;
   }

   public boolean hasUsers() {
      return users != null && users.size() > 0;
   }

   public void setUsers(List<User> users) {
      this.users = users;
   }

   public static Links forUser(User user) {
      Links links = new Links();
      links.users = new ArrayList<>();
      links.users.add(user);
      return links;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {dest.writeTypedList(this.users);}

   public Links() {}

   protected Links(Parcel in) {this.users = in.createTypedArrayList(User.CREATOR);}

   public static final Creator<Links> CREATOR = new Creator<Links>() {
      @Override
      public Links createFromParcel(Parcel source) {return new Links(source);}

      @Override
      public Links[] newArray(int size) {return new Links[size];}
   };
}
