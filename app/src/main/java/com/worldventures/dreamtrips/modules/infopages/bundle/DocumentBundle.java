package com.worldventures.dreamtrips.modules.infopages.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.infopages.model.Document;

public class DocumentBundle implements Parcelable {

   private Document document;
   private String analyticsAction;

   public DocumentBundle(Document document, String analyticsAction) {
      this.document = document;
      this.analyticsAction = analyticsAction;
   }

   public Document getDocument() {
      return document;
   }

   public String getAnalyticsAction() {
      return analyticsAction;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeParcelable(this.document, flags);
      dest.writeString(analyticsAction);
   }

   protected DocumentBundle(Parcel in) {
      this.document = in.readParcelable(Document.class.getClassLoader());
      this.analyticsAction = in.readString();
   }

   public static final Creator<DocumentBundle> CREATOR = new Creator<DocumentBundle>() {
      @Override
      public DocumentBundle createFromParcel(Parcel source) {return new DocumentBundle(source);}

      @Override
      public DocumentBundle[] newArray(int size) {return new DocumentBundle[size];}
   };
}
