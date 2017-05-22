package com.worldventures.dreamtrips.modules.background_uploading.model.video;


import com.google.gson.annotations.SerializedName;

public class VideoProcessStatus {

   public static final String STATUS_RECEIVED   = "received";
   public static final String STATUS_UPLOADED   = "uploaded";
   public static final String STATUS_TRANSCODED = "transcoded";
   public static final String STATUS_ERROR      = "error";
   public static final String STATUS_COMPLETED  = "completed";

   @SerializedName("tempId")
   private String tempId;

   @SerializedName("assetStatus")
   private String assetStatus;

   @SerializedName("errorMsg")
   private String errorMessage;

   public VideoProcessStatus() {
   }

   public String getTempId() {
      return tempId;
   }

   public String getAssetStatus() {
      return assetStatus;
   }

   public String getErrorMessage() {
      return errorMessage;
   }
}
