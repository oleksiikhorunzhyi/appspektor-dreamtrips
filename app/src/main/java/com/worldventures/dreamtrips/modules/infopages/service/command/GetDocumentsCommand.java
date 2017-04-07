package com.worldventures.dreamtrips.modules.infopages.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.documents.GetDocumentsHttpAction;
import com.worldventures.dreamtrips.core.api.action.MappableApiActionCommand;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.storage.KeyValueStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedStorage;
import com.worldventures.dreamtrips.modules.infopages.model.Document;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetDocumentsCommand extends MappableApiActionCommand<GetDocumentsHttpAction,
      List<Document>, Document> implements CachedAction<List<Document>> {

   protected static final int PER_PAGE = 10;

   protected List<Document> cachedDocuments;

   private boolean refresh;
   private DocumentType documentType;

   public GetDocumentsCommand(DocumentType documentType) {
      this(documentType, false);
   }

   public GetDocumentsCommand(DocumentType documentType, boolean refresh) {
      this.refresh = refresh;
      this.documentType = documentType;
   }

   @Override
   protected Object mapHttpActionResult(GetDocumentsHttpAction httpAction) {
      return httpAction.response();
   }

   @Override
   protected GetDocumentsHttpAction getHttpAction() {
      com.worldventures.dreamtrips.api.documents.model.DocumentType type =
         mapperyContext.convert(documentType, com.worldventures.dreamtrips.api.documents.model.DocumentType.class);
      return new GetDocumentsHttpAction(type, getPage(), PER_PAGE);
   }

   @Override
   protected Class<GetDocumentsHttpAction> getHttpActionClass() {
      return GetDocumentsHttpAction.class;
   }

   @Override
   protected Class<Document> getMappingTargetClass() {
      return Document.class;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_documents;
   }

   @Override
   public void onRestore(ActionHolder holder, List<Document> cache) {
      this.cachedDocuments = new ArrayList<>(cache);
   }

   @Override
   public List<Document> getCacheData() {
      return new ArrayList<>(getResult());
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundle cacheBundle = new CacheBundleImpl();
      cacheBundle.put(PaginatedStorage.BUNDLE_REFRESH, refresh);
      cacheBundle.put(KeyValueStorage.BUNDLE_KEY_VALUE, documentType.toString());
      return ImmutableCacheOptions.builder().params(cacheBundle).build();
   }

   @Override
   protected void onSuccess(CommandCallback<List<Document>> callback, List<Document> documents) {
      clearCacheIfNeeded();
      super.onSuccess(callback, documents);
   }

   public boolean isNoMoreElements() {
      return getResult().size() < PER_PAGE;
   }

   public boolean isRefreshCommand() {
      return refresh;
   }

   private void clearCacheIfNeeded() {
      if (refresh) cachedDocuments = null;
   }

   protected int getPage() {
      if (refresh || cachedDocuments == null || cachedDocuments.isEmpty()) {
         return 1;
      }
      return cachedDocuments.size() / PER_PAGE + 1;
   }

   public List<Document> items() {
      List<Document> documents = new ArrayList<>();
      if (cachedDocuments != null) documents.addAll(cachedDocuments);
      if (getResult() != null) documents.addAll(getResult());
      return documents;
   }

   public enum DocumentType {
      HELP,
      LEGAL,
      SMARTCARD
   }
}
