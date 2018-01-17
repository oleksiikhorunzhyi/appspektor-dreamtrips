package com.worldventures.core.modules.video.model;


import com.worldventures.core.model.Filterable;

import java.io.Serializable;
import java.util.List;

public class VideoLocale implements Serializable, Filterable {

   private final String title;
   private final String country;
   private final String icon;
   private final List<VideoLanguage> languages;

   public VideoLocale() {
      this.title = null;
      this.country = null;
      this.icon = null;
      this.languages = null;
   }

   public VideoLocale(String title, String country, String icon, List<VideoLanguage> languages) {
      this.title = title;
      this.country = country;
      this.icon = icon;
      this.languages = languages;
   }

   public String getTitle() {
      return title;
   }

   public String getCountry() {
      return country;
   }

   public String getImage() {
      return icon;
   }

   @Override
   public String toString() {
      return title;
   }

   @SuppressWarnings("all")
   @Override
   public boolean equals(Object o) {
      if (this == o) { return true; }
      if (o == null || getClass() != o.getClass()) { return false; }

      VideoLocale that = (VideoLocale) o;

      if (title != null ? !title.equals(that.title) : that.title != null) { return false; }
      return !(country != null ? !country.equals(that.country) : that.country != null);
   }

   @Override
   public int hashCode() {
      int result = title != null ? title.hashCode() : 0;
      result = 31 * result + (country != null ? country.hashCode() : 0);
      return result;
   }

   public List<VideoLanguage> getLanguages() {
      return languages;
   }

   @Override
   public boolean containsQuery(String query) {
      return (title != null && title.toLowerCase().contains(query.toLowerCase()))
            || (country != null && country.toLowerCase().contains(query.toLowerCase()));
   }
}
