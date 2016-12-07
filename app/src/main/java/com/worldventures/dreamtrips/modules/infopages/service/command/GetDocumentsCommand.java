package com.worldventures.dreamtrips.modules.infopages.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
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

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mappery;

   private List<Document> cachedDocuments;

   @Override
   protected void run(CommandCallback<List<Document>> callback) throws Throwable {
//      janet.createPipe(GetDocumentsHttpAction.class)
//            .createObservableResult(new GetDocumentsHttpAction())
//            .map(action -> mappery.convert(action.response(), Document.class))
//            .subscribe(callback::onSuccess, callback::onFail);
      callback.onSuccess(stub());
   }

   public List<Document> items() {
      if (getResult() != null) return getResult();
      else return cachedDocuments;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_documents;
   }

   private List<Document> stub() {
      List<Document> documentList = new ArrayList<>();
      documentList.add(new Document(1, "Google", "https://google.com.ua"));
      documentList.add(new Document(2, "Amazon", "https://amazon.com"));
      return documentList;
   }

   @Override
   public List<Document> getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, List<Document> cache) {
      this.cachedDocuments = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder().build();
   }
}
