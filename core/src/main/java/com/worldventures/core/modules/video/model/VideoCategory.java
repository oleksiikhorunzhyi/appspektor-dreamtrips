package com.worldventures.core.modules.video.model;

import java.util.List;

public class VideoCategory {

   private final String category;
   private final List<Video> videos;

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
