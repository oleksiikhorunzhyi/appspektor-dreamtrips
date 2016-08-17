package com.worldventures.dreamtrips.core.component;

import java.util.List;

public class ComponentsConfig {
   private final List<String> activeComponents;

   public ComponentsConfig(List<String> activeComponents) {
      this.activeComponents = activeComponents;
   }

   public List<String> getActiveComponents() {
      return activeComponents;
   }
}
