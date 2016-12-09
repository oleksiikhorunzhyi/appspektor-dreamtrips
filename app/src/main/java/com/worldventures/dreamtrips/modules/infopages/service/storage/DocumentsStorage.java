package com.worldventures.dreamtrips.modules.infopages.service.storage;

import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedCombinedStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedDiskStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedMemoryStorage;
import com.worldventures.dreamtrips.modules.infopages.model.Document;
import com.worldventures.dreamtrips.modules.infopages.service.command.GetDocumentsCommand;

import java.util.List;

public class DocumentsStorage extends PaginatedCombinedStorage<Document> implements ActionStorage<List<Document>> {

   public DocumentsStorage(PaginatedMemoryStorage<Document> memoryStorage, PaginatedDiskStorage<Document> diskStorage) {
      super(memoryStorage, diskStorage);
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetDocumentsCommand.class;
   }
}
