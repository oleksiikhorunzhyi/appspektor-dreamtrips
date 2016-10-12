package com.worldventures.dreamtrips.modules.video.model;

import java.util.List;

public class VideoCategory {
   private String category;
   private List<Video> videos;

   public VideoCategory(String category, List<Video> videos) {
      this.category = category;
      this.videos = videos;
   }

   public String getCategory() {
      return category;
   }

   public List<Video> getVideos() {
      return videos;
   }
}
