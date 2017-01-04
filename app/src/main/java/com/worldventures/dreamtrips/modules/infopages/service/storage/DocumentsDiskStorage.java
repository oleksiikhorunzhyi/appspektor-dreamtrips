package com.worldventures.dreamtrips.modules.infopages.service.storage;

import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedDiskStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.infopages.model.Document;

import java.util.List;

import rx.functions.Action1;
import rx.functions.Func0;

public class DocumentsDiskStorage extends PaginatedDiskStorage<Document> {

   private SnappyRepository db;

   public DocumentsDiskStorage(SnappyRepository db) {
      this.db = db;
   }

   @Override
   public Func0<List<Document>> getRestoreAction() {
      return db::getDocuments;
   }

   @Override
   public Action1<List<Document>> getSaveAction() {
      return db::setDocuments;
   }
}
