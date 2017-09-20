package com.worldventures.dreamtrips.social.ui.friends.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.social.domain.storage.SocialSnappyRepository;
import com.worldventures.dreamtrips.social.ui.friends.service.command.GetCirclesCommand;
import com.worldventures.dreamtrips.social.ui.friends.model.Circle;

import java.util.List;

public class CirclesStorage implements ActionStorage<List<Circle>> {

   private final SocialSnappyRepository db;

   public CirclesStorage(SocialSnappyRepository db) {
      this.db = db;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetCirclesCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, List<Circle> data) {
      db.saveCircles(data);
   }

   @Override
   public List<Circle> get(@Nullable CacheBundle action) {
      return db.getCircles();
   }
}
