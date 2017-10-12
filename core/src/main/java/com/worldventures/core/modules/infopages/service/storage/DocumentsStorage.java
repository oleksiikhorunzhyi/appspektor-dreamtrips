package com.worldventures.core.modules.infopages.service.storage;

import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.core.janet.cache.storage.CombinedListStorage;
import com.worldventures.core.janet.cache.storage.Storage;
import com.worldventures.core.modules.infopages.model.Document;
import com.worldventures.core.modules.infopages.service.command.GetDocumentsCommand;

import java.util.List;

public class DocumentsStorage extends CombinedListStorage<Document> implements ActionStorage<List<Document>> {

   public DocumentsStorage(Storage<List<Document>> memoryStorage, Storage<List<Document>> diskStorage) {
      super(memoryStorage, diskStorage);
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetDocumentsCommand.class;
   }
}
