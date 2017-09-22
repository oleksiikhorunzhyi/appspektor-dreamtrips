package com.worldventures.dreamtrips.social.ui.infopages.service.storage;

import com.worldventures.dreamtrips.core.janet.cache.storage.KeyValuePaginatedDiskStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.social.ui.infopages.model.Document;

import java.util.List;

import rx.functions.Action2;
import rx.functions.Func1;

public class DocumentsDiskStorage extends KeyValuePaginatedDiskStorage<Document> {

   private SnappyRepository db;

   public DocumentsDiskStorage(SnappyRepository db) {
      this.db = db;
   }

   @Override
   public Action2<String, List<Document>> getSaveAction() {
      return db::setDocuments;
   }

   @Override
   public Func1<String, List<Document>> getRestoreFunc() {
      return db::getDocuments;
   }
}
