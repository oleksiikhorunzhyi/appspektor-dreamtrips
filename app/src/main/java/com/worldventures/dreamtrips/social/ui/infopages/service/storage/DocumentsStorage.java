package com.worldventures.dreamtrips.social.ui.infopages.service.storage;

import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.CombinedListStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.Storage;
import com.worldventures.dreamtrips.social.ui.infopages.model.Document;
import com.worldventures.dreamtrips.social.ui.infopages.service.command.GetDocumentsCommand;

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
