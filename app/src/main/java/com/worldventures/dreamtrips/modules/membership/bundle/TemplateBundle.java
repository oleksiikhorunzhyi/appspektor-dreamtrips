package com.worldventures.dreamtrips.modules.membership.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;

public class TemplateBundle implements Parcelable {

   private InviteTemplate inviteTemplate;

   public TemplateBundle(InviteTemplate inviteTemplate) {
      this.inviteTemplate = inviteTemplate;
   }

   protected TemplateBundle(Parcel in) {
      inviteTemplate = in.readParcelable(InviteTemplate.class.getClassLoader());
   }

   public static final Creator<TemplateBundle> CREATOR = new Creator<TemplateBundle>() {
      @Override
      public TemplateBundle createFromParcel(Parcel in) {
         return new TemplateBundle(in);
      }

      @Override
      public TemplateBundle[] newArray(int size) {
         return new TemplateBundle[size];
      }
   };

   public InviteTemplate getInviteTemplate() {
      return inviteTemplate;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel parcel, int i) {
      parcel.writeParcelable(inviteTemplate, i);
   }
}
