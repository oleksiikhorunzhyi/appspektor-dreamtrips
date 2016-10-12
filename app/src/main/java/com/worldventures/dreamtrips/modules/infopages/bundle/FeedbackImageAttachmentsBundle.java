package com.worldventures.dreamtrips.modules.infopages.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;

import java.util.ArrayList;
import java.util.List;

public class FeedbackImageAttachmentsBundle implements Parcelable {

   private int position;
   private List<FeedbackImageAttachment> attachments;

   public FeedbackImageAttachmentsBundle(int position, List<FeedbackImageAttachment> attachments) {
      this.position = position;
      this.attachments = attachments;
   }

   public int getPosition() {
      return position;
   }

   public List<FeedbackImageAttachment> getAttachments() {
      return attachments;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(this.position);
      dest.writeTypedList(this.attachments);
   }

   protected FeedbackImageAttachmentsBundle(Parcel in) {
      this.position = in.readInt();
      this.attachments = in.createTypedArrayList(FeedbackImageAttachment.CREATOR);
   }

   public static final Creator<FeedbackImageAttachmentsBundle> CREATOR = new Creator<FeedbackImageAttachmentsBundle>() {
      @Override
      public FeedbackImageAttachmentsBundle createFromParcel(Parcel source) {return new FeedbackImageAttachmentsBundle(source);}

      @Override
      public FeedbackImageAttachmentsBundle[] newArray(int size) {return new FeedbackImageAttachmentsBundle[size];}
   };
}
