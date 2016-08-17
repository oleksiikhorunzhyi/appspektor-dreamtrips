package com.worldventures.dreamtrips.modules.feed.event;

public class DownloadPhotoEvent {

   public final String url;

   public DownloadPhotoEvent(String url) {
      this.url = url;
   }
}
