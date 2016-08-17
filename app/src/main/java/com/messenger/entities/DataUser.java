package com.messenger.entities;

import android.net.Uri;
import android.os.Parcel;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.messenger.messengerservers.model.MessengerUser;
import com.messenger.storage.MessengerDatabase;
import com.messenger.ui.model.ChatUser;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.BaseProviderModel;

@TableEndpoint(name = DataUser.TABLE_NAME, contentProviderName = MessengerDatabase.NAME)
@Table(tableName = DataUser.TABLE_NAME, databaseName = MessengerDatabase.NAME, insertConflict = ConflictAction.REPLACE)
public class DataUser extends BaseProviderModel<DataUser> implements ChatUser, Comparable<DataUser> {
   public static final String TABLE_NAME = "Users";

   @ContentUri(path = TABLE_NAME, type = ContentUri.ContentType.VND_MULTIPLE + TABLE_NAME) public static final Uri CONTENT_URI = MessengerDatabase
         .buildUri(TABLE_NAME);

   @PrimaryKey @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE) @Column(name = BaseColumns._ID)
   String id;
   @Column int socialId;
   @Column String firstName;
   @Column String lastName;
   @Column boolean online;
   @Column boolean host;
   @Column String userAvatarUrl;
   @Column Boolean friend;

   public DataUser() {
   }

   public DataUser(MessengerUser messengerUser) {
      this(messengerUser.getName());
      setOnline(messengerUser.isOnline());
   }

   public DataUser(String userId) {
      this.id = userId;
   }

   public String getFirstName() {
      return firstName;
   }

   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   public String getLastName() {
      return lastName;
   }

   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   public void setId(String id) {
      this.id = id;
   }

   @Override
   public String getId() {
      return id;
   }

   public int getSocialId() {
      return socialId;
   }

   public void setSocialId(int socialId) {
      this.socialId = socialId;
   }

   @Override
   public String getName() {
      return getDisplayedName();
   }

   public String getDisplayedName() {
      return String.format("%s %s", firstName, lastName);
   }

   public String getAvatarUrl() {
      return userAvatarUrl;
   }

   public void setAvatarUrl(String avatarUrl) {
      this.userAvatarUrl = avatarUrl;
   }

   public boolean isOnline() {
      return online;
   }

   public void setOnline(boolean online) {
      this.online = online;
   }

   public boolean isFriend() {
      return friend != null ? friend : false;
   }

   public void setFriend(Boolean friend) {
      this.friend = friend;
   }

   public boolean isHost() {
      return host;
   }

   public void setHost(boolean host) {
      this.host = host;
   }

   public boolean isFriendSet() {
      return friend != null;
   }

   @Override
   public boolean isCloseFriend() {
      return false;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      DataUser user = (DataUser) o;

      return !(id != null ? !id.equals(user.id) : user.id != null);
   }

   @Override
   public int hashCode() {
      return id.hashCode();
   }

   @Override
   public String toString() {
      return "User{" +
            "_id='" + id + '\'' +
            '}';
   }

   @Override
   public Uri getDeleteUri() {
      return CONTENT_URI;
   }

   @Override
   public Uri getInsertUri() {
      return CONTENT_URI;
   }

   @Override
   public Uri getUpdateUri() {
      return CONTENT_URI;
   }

   @Override
   public Uri getQueryUri() {
      return CONTENT_URI;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.id);
      dest.writeInt(this.socialId);
      dest.writeString(this.firstName);
      dest.writeString(this.lastName);
      dest.writeByte(online ? (byte) 1 : (byte) 0);
      dest.writeByte(host ? (byte) 1 : (byte) 0);
      dest.writeString(this.userAvatarUrl);
      dest.writeValue(this.friend);
   }

   protected DataUser(Parcel in) {
      this.id = in.readString();
      this.socialId = in.readInt();
      this.firstName = in.readString();
      this.lastName = in.readString();
      this.online = in.readByte() != 0;
      this.host = in.readByte() != 0;
      this.userAvatarUrl = in.readString();
      this.friend = (Boolean) in.readValue(Boolean.class.getClassLoader());
   }

   public static final Creator<DataUser> CREATOR = new Creator<DataUser>() {
      public DataUser createFromParcel(Parcel source) {
         return new DataUser(source);
      }

      public DataUser[] newArray(int size) {
         return new DataUser[size];
      }
   };

   @Override
   public int compareTo(@NonNull DataUser another) {
      return id.compareTo(another.getId());
   }
}

