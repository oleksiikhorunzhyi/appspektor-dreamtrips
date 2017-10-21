package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.janet.cache.CacheOptions;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.transactions.DetailTransactionThrst;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.transactions.ThrstTransactionResponse;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.TransactionDetailActionParams;
import com.worldventures.dreamtrips.modules.dtl.service.action.http.GetTransactionsHttpAction;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetTransactionsCommand extends CommandWithError<List<DetailTransactionThrst>>
      implements InjectableAction, CachedAction<List<DetailTransactionThrst>> {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;
   @Inject SessionHolder sessionHolder;

   private TransactionDetailActionParams reviewParams;
   private boolean saveToCache;
   private boolean readFromCache;

   private List<DetailTransactionThrst> cachedItems;
   private ThrstTransactionResponse thrstTransactionResponse;

   public static GetTransactionsCommand loadFromNetworkCommand(TransactionDetailActionParams reviewParams) {
      return new GetTransactionsCommand(reviewParams);
   }

   public static GetTransactionsCommand readCurrentTransactionsCommand() {
      return new GetTransactionsCommand();
   }

   private GetTransactionsCommand(TransactionDetailActionParams reviewParams) {
      this.reviewParams = reviewParams;
      this.saveToCache = true;
      this.readFromCache = false;
   }

   private GetTransactionsCommand() {
      this.saveToCache = false;
      this.readFromCache = true;
   }

   @Override
   protected void run(CommandCallback<List<DetailTransactionThrst>> callback) throws Throwable {
      if (readFromCache) callback.onSuccess(cachedItems);
      else {
         janet.createPipe(GetTransactionsHttpAction.class)
               .createObservableResult(new GetTransactionsHttpAction(reviewParams.take(), reviewParams.skip(),
                     reviewParams.localeId(), sessionHolder.get().get().getUsername(),
                     sessionHolder.get().get().getLegacyApiToken()))
               .map(GetTransactionsHttpAction::getResponse)
               .doOnNext(response -> thrstTransactionResponse = response)
               .map(ThrstTransactionResponse::results)
               .subscribe(callback::onSuccess, callback::onFail);
      }
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.dtl_load_error;
   }

   @Override
   public List<DetailTransactionThrst> getCacheData() {
      return new ArrayList<>(getResult());
   }

   @Override
   public void onRestore(ActionHolder holder, List<DetailTransactionThrst> cache) {
      cachedItems = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .restoreFromCache(readFromCache)
            .saveToCache(saveToCache)
            .build();
   }

   @Nullable
   public ThrstTransactionResponse getThrstTransactionResponse() {
      return thrstTransactionResponse;
   }
}
