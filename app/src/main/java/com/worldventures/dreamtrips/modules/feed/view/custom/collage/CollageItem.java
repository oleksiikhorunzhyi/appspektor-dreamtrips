package com.worldventures.dreamtrips.modules.feed.view.custom.collage;

public class CollageItem {
   private final String url;
   private final String highResUrl;
   private final int width;
   private final int height;

   public CollageItem(String url, String highResUrl, int width, int height) {
      this.url = url;
      this.highResUrl = highResUrl;
      this.width = width;
      this.height = height;
   }

   public String url() {
      return url;
   }

   public String highResUrl() {
      return highResUrl;
   }

   public int width() {
      return width;
   }

   public int height() {
      return height;
   }

   @Override
   public String toString() {
      return "CollageItem{" +
            "url='" + url + '\'' +
            ", width=" + width +
            ", height=" + height +
            '}';
   }
}
