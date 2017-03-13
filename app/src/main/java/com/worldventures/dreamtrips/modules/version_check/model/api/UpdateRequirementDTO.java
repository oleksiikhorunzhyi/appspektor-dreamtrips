package com.worldventures.dreamtrips.modules.version_check.model.api;

import java.util.List;

public class UpdateRequirementDTO {

   private String scope;
   private List<Category> categories;

   public String getScope() {
      return scope;
   }

   public void setScope(String scope) {
      this.scope = scope;
   }

   public List<Category> getCategories() {
      return categories;
   }

   public void setCategories(List<Category> categories) {
      this.categories = categories;
   }
}
