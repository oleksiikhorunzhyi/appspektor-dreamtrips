package com.worldventures.dreamtrips.modules.membership.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.worldventures.dreamtrips.modules.common.view.util.Filterable;

public class Member implements Parcelable, Filterable {

   private String id;
   private String name;
   private String email;
   private String phone;
   private boolean emailIsMain = true;
   private boolean isChecked;
   private SentInvite sentInvite;

   public Member() {
   }

   private int originalPosition;

   public SentInvite getSentInvite() {
      return sentInvite;
   }

   public void setSentInvite(SentInvite sentInvite) {
      this.sentInvite = sentInvite;
   }

   public void setEmailIsMain(boolean emailIsMain) {
      this.emailIsMain = emailIsMain;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getPhone() {
      return phone;
   }

   public void setPhone(String phone) {
      this.phone = phone;
   }

   public String getSubtitle() {
      return emailIsMain ? email : phone;
   }

   public boolean isChecked() {
      return isChecked;
   }

   public void setIsChecked(boolean isChecked) {
      this.isChecked = isChecked;
   }

   public boolean isEmailMain() {
      return emailIsMain;
   }

   public int getOriginalPosition() {
      return originalPosition;
   }

   public void setOriginalPosition(int originalPosition) {
      this.originalPosition = originalPosition;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Member member = (Member) o;

      boolean nameEq = name != null ? name.equals(member.name) : member.name == null;
      boolean emailEq = email != null ? email.equals(member.email) : member.email == null;
      boolean phoneEq = phone != null ? phone.equals(member.phone) : member.phone == null;
      if (emailIsMain && nameEq && emailEq) return true;
      else return nameEq && phoneEq;
   }

   @Override
   public int hashCode() {
      int result = name != null ? name.hashCode() : 0;
      result = 31 * result + (email != null ? email.hashCode() : 0);
      result = 31 * result + (phone != null ? phone.hashCode() : 0);
      return result;
   }

   @Override
   public boolean containsQuery(String query) {
      return !(query == null || TextUtils.isEmpty(query.trim())) && ((name != null && name.toLowerCase()
            .contains(query)) || (email != null && email.toLowerCase()
            .contains(query)) || (phone != null && phone.contains(query)));
   }

   protected Member(Parcel in) {
      id = in.readString();
      name = in.readString();
      email = in.readString();
      phone = in.readString();
      emailIsMain = in.readByte() != 0;
      isChecked = in.readByte() != 0;
      sentInvite = in.readParcelable(SentInvite.class.getClassLoader());
      originalPosition = in.readInt();
   }

   public static final Creator<Member> CREATOR = new Creator<Member>() {
      @Override
      public Member createFromParcel(Parcel in) {
         return new Member(in);
      }

      @Override
      public Member[] newArray(int size) {
         return new Member[size];
      }
   };

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(id);
      dest.writeString(name);
      dest.writeString(email);
      dest.writeString(phone);
      dest.writeByte((byte) (emailIsMain ? 1 : 0));
      dest.writeByte((byte) (isChecked ? 1 : 0));
      dest.writeParcelable(sentInvite, flags);
      dest.writeInt(originalPosition);
   }
}
