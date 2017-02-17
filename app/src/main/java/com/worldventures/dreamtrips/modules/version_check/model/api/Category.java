package com.worldventures.dreamtrips.modules.version_check.model.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Category {

   @SerializedName("category")
   private String name;
   private List<ConfigSetting> configSettings;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List<ConfigSetting> getConfigSettings() {
      return configSettings;
   }

   public void setConfigSettings(List<ConfigSetting> configSettings) {
      this.configSettings = configSettings;
   }
}
