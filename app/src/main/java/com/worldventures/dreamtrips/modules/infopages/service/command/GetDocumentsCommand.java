package com.worldventures.dreamtrips.modules.infopages.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.documents.GetDocumentsHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedStorage;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.infopages.model.Document;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetDocumentsCommand extends CommandWithError<List<Document>> implements InjectableAction,
      CachedAction<List<Document>> {

   private static final int PER_PAGE = 10;

   @Inject Janet janet;
   @Inject MapperyContext mappery;

   private List<Document> cachedDocuments;

   private boolean refresh;

   public GetDocumentsCommand() {
      this.refresh = false;
   }

   public GetDocumentsCommand(boolean refresh) {
      this.refresh = refresh;
   }

   @Override
   protected void run(CommandCallback<List<Document>> callback) throws Throwable {
      janet.createPipe(GetDocumentsHttpAction.class)
            .createObservableResult(new GetDocumentsHttpAction(getPage(), PER_PAGE))
            .map(action -> mappery.convert(action.response(), Document.class))
            .doOnNext(list -> clearCacheIfNeeded())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   public List<Document> items() {
      List<Document> documents = new ArrayList<>();
      if (cachedDocuments != null) documents.addAll(cachedDocuments);
      if (getResult() != null) documents.addAll(getResult());
      return documents;
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
      return ImmutableCacheOptions.builder().params(cacheBundle).build();
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

   private int getPage() {
      if (refresh || cachedDocuments == null || cachedDocuments.isEmpty()) {
         return 1;
      }
      return cachedDocuments.size() / PER_PAGE + 1;
   }
}
