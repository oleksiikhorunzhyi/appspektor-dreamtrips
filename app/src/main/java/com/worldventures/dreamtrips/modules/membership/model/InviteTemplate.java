package com.worldventures.dreamtrips.modules.membership.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.techery.spares.adapter.HeaderItem;

import java.util.ArrayList;
import java.util.List;

public class InviteTemplate implements Parcelable, HeaderItem {

   private int id;
   private String title;
   private Type type;
   private String category;
   private String coverUrl;
   private String video;
   private String locale;
   private String content;

   private List<Member> to = new ArrayList<>();
   private String from;
   private String link;
   private String name;

   public InviteTemplate(int id, String title, Type type, String category, String coverUrl, String video, String locale, String content) {
      this.id = id;
      this.title = title;
      this.type = type;
      this.category = category;
      this.coverUrl = coverUrl;
      this.video = video;
      this.locale = locale;
      this.content = content;
   }

   public InviteTemplate(int id, String title, String coverUrl, String video, String link, String locale, String content) {
      this.id = id;
      this.title = title;
      this.coverUrl = coverUrl;
      this.video = video;
      this.link = link;
      this.locale = locale;
      this.content = content;
   }

   public int getId() {
      return id;
   }

   public String getCategory() {
      return category;
   }

   public void setCategory(String category) {
      this.category = category;
   }

   public List<Member> getTo() {
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

   public String getCoverUrl() {
      return coverUrl;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
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

   public void setContent(String content) {
      this.content = content;
   }

   public Type getType() {
      return type;
   }

   public void setType(Type type) {
      this.type = type;
   }

   public String getLink() {
      return link;
   }

   public void setLink(String link) {
      this.link = link;
   }

   @Override
   public String getHeaderTitle() {
      return getCategory();
   }

   public enum Type {
      EMAIL, SMS;

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
      dest.writeString(this.coverUrl);
      dest.writeString(this.video);
      dest.writeString(this.locale);
      dest.writeString(this.content);
      Member membersArray[] = new Member[to.size()];
      dest.writeParcelableArray(to.toArray(membersArray), 0);
      dest.writeString(this.from);
      dest.writeInt(this.type == null ? -1 : this.type.ordinal());
      dest.writeString(this.link);
      dest.writeInt(this.id);
      dest.writeString(this.name);
   }

   private InviteTemplate(Parcel in) {
      this.title = in.readString();
      this.coverUrl = in.readString();
      this.video = in.readString();
      this.locale = in.readString();
      this.content = in.readString();
      Parcelable[] membersArray = in.readParcelableArray(Member.class.getClassLoader());
      for (Parcelable member : membersArray) {
         this.to.add((Member) member);
      }
      this.from = in.readString();
      int tmpType = in.readInt();
      this.type = tmpType == -1 ? null : Type.values()[tmpType];
      this.link = in.readString();
      this.id = in.readInt();
      this.name = in.readString();
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
