package com.worldventures.core.model.session;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.model.User;

public class FeatureManager {

   private SessionHolder sessionHolder;

   public FeatureManager(SessionHolder sessionHolder) {
      this.sessionHolder = sessionHolder;
   }

   public boolean available(@Feature.FeatureName String name) {
      UserSession userSession = sessionHolder.get().orNull();
      return !(userSession == null || userSession.getFeatures() == null) && Queryable.from(userSession.getFeatures())
            .any(f -> f.name.equals(name));
   }

   public void checkIf(@Feature.FeatureName String name, OnFeatureChecked onFeatureChecked) {
      onFeatureChecked.onChecked(available(name));
   }

   public void with(@Feature.FeatureName String name, OnFeatureAvailable onAvailable) {
      with(name, onAvailable, null);
   }

   public void without(@Feature.FeatureName String name, OnFeatureMissing onMissing) {
      with(name, null, onMissing);
   }

   public void with(@Feature.FeatureName String name, OnFeatureAvailable onAvailable, OnFeatureMissing onMissing) {
      if (available(name)) {
         if (onAvailable != null) { onAvailable.onAvailable(); }
      } else {
         if (onMissing != null) { onMissing.onMissing(); }
      }
   }

   public boolean isUserInfoAvailable(User user) {
      return available(Feature.SOCIAL) || user.getId() == sessionHolder.get().get().getUser().getId();
   }

   public interface OnFeatureChecked {
      void onChecked(boolean isAvailable);
   }

   public interface OnFeatureAvailable {
      void onAvailable();
   }

   public interface OnFeatureMissing {
      void onMissing();
   }

}
