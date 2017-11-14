package com.worldventures.dreamtrips.social.ui.background_uploading.model.video;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoProcessBunchStatus {

   @SerializedName("status")
   private int videoStatusCount;

   @SerializedName("message")
   private String message;

   @SerializedName("content")
   private List<VideoProcessStatus> videoProcessStatuses;

   public int getVideoStatusCount() {
      return videoStatusCount;
   }

   public String getMessage() {
      return message;
   }

   public List<VideoProcessStatus> getVideoProcessStatuses() {
      return videoProcessStatuses;
   }
}
