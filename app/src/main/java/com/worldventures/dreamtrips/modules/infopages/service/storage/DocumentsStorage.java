package com.worldventures.dreamtrips.modules.infopages.service.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.infopages.model.Document;
import com.worldventures.dreamtrips.modules.infopages.service.command.GetDocumentsCommand;

import java.util.List;

public class DocumentsStorage implements ActionStorage<List<Document>> {

   private SnappyRepository snappyRepository;

   public DocumentsStorage(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public void save(@Nullable CacheBundle params, List<Document> data) {
      snappyRepository.setDocuments(data);
   }

   @Override
   public List<Document> get(@Nullable CacheBundle action) {
      return snappyRepository.getDocuments();
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetDocumentsCommand.class;
   }
}
