package com.worldventures.dreamtrips.modules.reptools.model;

import com.worldventures.dreamtrips.modules.common.view.util.Filterable;

import java.io.Serializable;

public class VideoLocale implements Serializable, Filterable {
   String title;
   String country;
   String icon;
   VideoLanguage[] language;

   public String getTitle() {
      return title;
   }

   public String getCountry() {
      return country;
   }

   @Override
   public String toString() {
      return title;
   }

   public String getImage() {
      return icon;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public void setCountry(String country) {
      this.country = country;
   }

   public void setImage(String image) {
      this.icon = image;
   }

   public void setLanguage(VideoLanguage[] language) {
      this.language = language;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VideoLocale that = (VideoLocale) o;

      if (title != null ? !title.equals(that.title) : that.title != null) return false;
      return !(country != null ? !country.equals(that.country) : that.country != null);

   }

   @Override
   public int hashCode() {
      int result = title != null ? title.hashCode() : 0;
      result = 31 * result + (country != null ? country.hashCode() : 0);
      return result;
   }

   public VideoLanguage[] getLanguage() {
      return language;
   }

   @Override
   public boolean containsQuery(String query) {
      return title.toLowerCase().contains(query.toLowerCase()) || country.toLowerCase().contains(query.toLowerCase());
   }
}
