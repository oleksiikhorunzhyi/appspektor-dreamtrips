package com.worldventures.dreamtrips.modules.membership.model;

import com.worldventures.dreamtrips.modules.video.model.VideoLanguage;
import com.worldventures.dreamtrips.modules.video.model.VideoLocale;

public class MediaHeader {

   private String title;
   private boolean showLanguage;
   private VideoLocale videoLocale;
   private VideoLanguage videoLanguage;

   public MediaHeader(String title, boolean showLanguage) {
      this.title = title;
      this.showLanguage = showLanguage;
   }

   public MediaHeader(String title) {
      this.title = title;
   }

   public VideoLocale getVideoLocale() {
      return videoLocale;
   }

   public VideoLanguage getVideoLanguage() {
      return videoLanguage;
   }

   public void setVideoLanguage(VideoLanguage videoLanguage) {
      this.videoLanguage = videoLanguage;
   }

   public void setVideoLocale(VideoLocale videoLocale) {
      this.videoLocale = videoLocale;
   }

   public String getTitle() {
      return title;
   }

   public boolean isShowLanguage() {
      return showLanguage;
   }
}
