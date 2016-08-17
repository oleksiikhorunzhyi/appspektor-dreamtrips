package com.worldventures.dreamtrips.modules.membership.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.techery.spares.adapter.HeaderItem;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

import java.util.ArrayList;

public class InviteTemplate extends BaseEntity implements Parcelable, HeaderItem {

   private String title;
   private CoverImage coverImage;
   private String video;
   private String locale;
   private String content;
   private ArrayList<Member> to = new ArrayList<>(0);
   private String from;
   private Type type;
   private String link;
   private String name;
   private String category;

   public String getCategory() {
      return category;
   }

   public void setCategory(String category) {
      this.category = category;
   }

   public ArrayList<Member> getTo() {
      return to;
   }

   public void setTo(ArrayList<Member> to) {
      this.to = to;
   }

   public String getFrom() {
      return from;
   }

   public void setFrom(String from) {
      this.from = from;
   }

   public String getTitle() {
      return title;
   }

   public CoverImage getCoverImage() {
      return coverImage;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public String getVideo() {
      return video;
   }

   public String getLocale() {
      return locale;
   }

   public String getContent() {
      return content;
   }

   public Type getType() {
      return type;
   }

   public String getLink() {
      return link;
   }

   public void setLink(String link) {
      this.link = link;
   }

   public void setType(Type type) {
      this.type = type;
   }

   public void setCoverImage(CoverImage coverImage) {
      this.coverImage = coverImage;
   }

   public InviteTemplate() {
      super();
   }

   @Override
   public String getHeaderTitle() {
      return getCategory();
   }


   public enum Type {
      @SerializedName("email")EMAIL,
      @SerializedName("sms")SMS;

      public static Type from(int i) {
         return i == 0 ? EMAIL : SMS;
      }
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.title);
      dest.writeSerializable(this.coverImage);
      dest.writeString(this.video);
      dest.writeString(this.locale);
      dest.writeString(this.content);
      dest.writeSerializable(this.to);
      dest.writeString(this.from);
      dest.writeInt(this.type == null ? -1 : this.type.ordinal());
      dest.writeString(this.link);
      dest.writeInt(this.id);
      dest.writeString(this.name);
   }

   private InviteTemplate(Parcel in) {
      this.title = in.readString();
      this.coverImage = (CoverImage) in.readSerializable();
      this.video = in.readString();
      this.locale = in.readString();
      this.content = in.readString();
      this.to = (ArrayList<Member>) in.readSerializable();
      this.from = in.readString();
      int tmpType = in.readInt();
      this.type = tmpType == -1 ? null : Type.values()[tmpType];
      this.link = in.readString();
      this.id = in.readInt();
      this.name = in.readString();
   }

   public void setContent(String content) {
      this.content = content;
   }

   public static final Creator<InviteTemplate> CREATOR = new Creator<InviteTemplate>() {
      public InviteTemplate createFromParcel(Parcel source) {
         return new InviteTemplate(source);
      }

      public InviteTemplate[] newArray(int size) {
         return new InviteTemplate[size];
      }
   };
}
