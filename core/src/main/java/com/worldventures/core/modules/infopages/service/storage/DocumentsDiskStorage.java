package com.worldventures.core.modules.infopages.service.storage;

import com.worldventures.janet.cache.storage.KeyValuePaginatedDiskStorage;
import com.worldventures.core.modules.infopages.model.Document;

import java.util.List;

import rx.functions.Action2;
import rx.functions.Func1;

public class DocumentsDiskStorage extends KeyValuePaginatedDiskStorage<Document> {

   private final InfopagesStorage db;

   public DocumentsDiskStorage(InfopagesStorage db) {
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
