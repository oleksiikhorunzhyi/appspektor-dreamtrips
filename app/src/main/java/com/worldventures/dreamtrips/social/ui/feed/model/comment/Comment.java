package com.worldventures.dreamtrips.social.ui.feed.model.comment;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.social.ui.feed.model.TranslatableItem;
import com.worldventures.dreamtrips.social.ui.feed.model.UidItem;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Parcelable, Serializable, UidItem, TranslatableItem {

   private String uid;
   private String parentId;
   private String text;
   private User user;
   private Date createdAt;
   private boolean update;
   private String language;

   private transient String translation;
   private transient boolean translated;

   public Comment() {
      //do nothing
   }

   protected Comment(Parcel in) {
      uid = in.readString();
      parentId = in.readString();
      text = in.readString();
      user = in.readParcelable(User.class.getClassLoader());
      language = in.readString();
   }

   public static final Creator<Comment> CREATOR = new Creator<Comment>() {
      @Override
      public Comment createFromParcel(Parcel in) {
         return new Comment(in);
      }

      @Override
      public Comment[] newArray(int size) {
         return new Comment[size];
      }
   };

   public void setUid(String uid) {
      this.uid = uid;
   }

   public void setParentId(String parentId) {
      this.parentId = parentId;
   }

   public void setText(String text) {
      this.text = text;
   }

   public void setUser(User user) {
      this.user = user;
   }

   public void setUpdate(boolean update) {
      this.update = update;
   }

   public void setLanguage(String language) {
      this.language = language;
   }

   public boolean isUpdate() {
      return update;
   }

   public String getMessage() {
      return text;
   }

   public void setMessage(String text) {
      this.text = text;
   }

   public User getOwner() {
      return user;
   }

   public Date getCreatedAt() {
      return createdAt;
   }

   @Override
   public String getUid() {
      return uid;
   }

   public void setCreatedAt(Date createdAt) {
      this.createdAt = createdAt;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel parcel, int i) {
      parcel.writeString(uid);
      parcel.writeString(parentId);
      parcel.writeString(text);
      parcel.writeParcelable(user, i);
      parcel.writeString(language);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      Comment comment = (Comment) o;

      return !(uid != null ? !uid.equals(comment.uid) : comment.uid != null);

   }

   @Override
   public int hashCode() {
      return uid != null ? uid.hashCode() : 0;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Translation
   ///////////////////////////////////////////////////////////////////////////

   public String getLanguage() {
      return language;
   }

   @Override
   public String getOriginalText() {
      return text;
   }

   @Nullable
   @Override
   public String getTranslation() {
      return translation;
   }

   @Override
   public void setTranslation(String translation) {
      this.translation = translation;
   }

   @Override
   public boolean isTranslated() {
      return translated;
   }

   @Override
   public void setTranslated(boolean translated) {
      this.translated = translated;
   }
}
