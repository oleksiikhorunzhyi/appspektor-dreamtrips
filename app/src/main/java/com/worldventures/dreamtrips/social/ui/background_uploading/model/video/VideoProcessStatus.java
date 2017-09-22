package com.worldventures.dreamtrips.social.ui.background_uploading.model.video;


import com.google.gson.annotations.SerializedName;

public class VideoProcessStatus {

   public static final String STATUS_RECEIVED   = "received";
   public static final String STATUS_UPLOADED   = "uploaded";
   public static final String STATUS_TRANSCODED = "transcoded";
   public static final String STATUS_ERROR      = "error";
   public static final String STATUS_COMPLETED  = "completed";

   @SerializedName("assetId")
   private String assetId;

   @SerializedName("assetStatus")
   private String assetStatus;

   @SerializedName("errorMsg")
   private String errorMessage;

   public VideoProcessStatus() {
   }

   public String getAssetId() {
      return assetId;
   }

   public String getAssetStatus() {
      return assetStatus;
   }

   public String getErrorMessage() {
      return errorMessage;
   }
}
