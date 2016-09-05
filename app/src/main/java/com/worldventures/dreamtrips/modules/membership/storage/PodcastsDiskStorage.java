package com.worldventures.dreamtrips.modules.membership.storage;

import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedDiskStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;

import java.util.List;

import rx.functions.Action1;
import rx.functions.Func0;

public class PodcastsDiskStorage extends PaginatedDiskStorage<Podcast> {

   private SnappyRepository db;

   public PodcastsDiskStorage(SnappyRepository db) {
      this.db = db;
   }

   @Override
   public Func0<List<Podcast>> getRestoreAction() {
      return db::getPodcasts;
   }

   @Override
   public Action1<List<Podcast>> getSaveAction() {
      return db::savePodcasts;
   }
}
