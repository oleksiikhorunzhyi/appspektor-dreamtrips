package com.worldventures.dreamtrips.modules.bucketlist.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PhotoUploadResponse implements Parcelable {
   InternalResponse file;

   public String getFieldname() {
      return file.fieldname;
   }

   public String getOriginalname() {
      return file.originalname;
   }

   public String getEncoding() {
      return file.encoding;
   }

   public String getMimetype() {
      return file.mimetype;
   }

   public String getLocation() {
      return file.location;
   }

   public PhotoUploadResponse() {
   }


   static class InternalResponse implements Parcelable {
      String fieldname;
      String originalname;
      String encoding;
      String mimetype;
      String location;


      @Override
      public int describeContents() {
         return 0;
      }

      @Override
      public void writeToParcel(Parcel dest, int flags) {
         dest.writeString(this.fieldname);
         dest.writeString(this.originalname);
         dest.writeString(this.encoding);
         dest.writeString(this.mimetype);
         dest.writeString(this.location);
      }

      public InternalResponse() {
      }

      protected InternalResponse(Parcel in) {
         this.fieldname = in.readString();
         this.originalname = in.readString();
         this.encoding = in.readString();
         this.mimetype = in.readString();
         this.location = in.readString();
      }

      public static final Creator<InternalResponse> CREATOR = new Creator<InternalResponse>() {
         public InternalResponse createFromParcel(Parcel source) {
            return new InternalResponse(source);
         }

         public InternalResponse[] newArray(int size) {
            return new InternalResponse[size];
         }
      };
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeParcelable(this.file, 0);
   }

   protected PhotoUploadResponse(Parcel in) {
      this.file = in.readParcelable(InternalResponse.class.getClassLoader());
   }

   public static final Creator<PhotoUploadResponse> CREATOR = new Creator<PhotoUploadResponse>() {
      public PhotoUploadResponse createFromParcel(Parcel source) {
         return new PhotoUploadResponse(source);
      }

      public PhotoUploadResponse[] newArray(int size) {
         return new PhotoUploadResponse[size];
      }
   };
}
