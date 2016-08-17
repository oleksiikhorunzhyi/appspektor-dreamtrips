package com.worldventures.dreamtrips.modules.membership.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

import java.util.Date;

public class History extends BaseEntity {
   @SerializedName("invitation_filled_template_id") int templateId;
   @SerializedName("contact") String contact;
   @SerializedName("type") InviteTemplate.Type type;
   @SerializedName("date") Date date;

   public History() {
      super();
   }

   public int getTemplateId() {
      return templateId;
   }

   public void setTemplateId(int templateId) {
      this.templateId = templateId;
   }

   public String getContact() {
      return contact;
   }

   public void setContact(String contact) {
      this.contact = contact;
   }

   public InviteTemplate.Type getType() {
      return type;
   }

   public void setType(InviteTemplate.Type type) {
      this.type = type;
   }

   public Date getDate() {
      return date;
   }

   public void setDate(Date date) {
      this.date = date;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeInt(this.templateId);
      dest.writeString(this.contact);
      dest.writeInt(this.type == null ? -1 : this.type.ordinal());
      dest.writeSerializable(this.date);
   }

   public History(Parcel in) {
      super(in);
      this.templateId = in.readInt();
      this.contact = in.readString();
      int tmpType = in.readInt();
      this.type = tmpType == -1 ? null : InviteTemplate.Type.values()[tmpType];
      this.date = (Date) in.readSerializable();
   }

   public static final Creator<History> CREATOR = new Creator<History>() {
      @Override
      public History createFromParcel(Parcel in) {
         return new History(in);
      }

      @Override
      public History[] newArray(int size) {
         return new History[size];
      }
   };
}
