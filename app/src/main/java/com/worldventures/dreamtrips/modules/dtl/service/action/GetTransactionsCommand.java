package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.transactions.ThrstTransactionResponse;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.TransactionDetailActionParams;
import com.worldventures.dreamtrips.modules.dtl.service.action.http.GetTransactionsHttpAction;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;
import com.worldventures.janet.cache.CacheBundle;
import com.worldventures.janet.cache.CacheBundleImpl;
import com.worldventures.janet.cache.CacheOptions;
import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.PaginatedStorage;
import com.worldventures.janet.injection.InjectableAction;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public final class GetTransactionsCommand extends CommandWithError<List<TransactionModel>>
      implements InjectableAction, CachedAction<List<TransactionModel>> {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;
   @Inject SessionHolder sessionHolder;

   private TransactionDetailActionParams reviewParams;
   private boolean saveToCache;
   private boolean readFromCache;
   private boolean refresh;

   private List<TransactionModel> cachedItems;

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
      this.refresh = reviewParams.skip() == 0;
   }

   private GetTransactionsCommand() {
      this.saveToCache = false;
      this.readFromCache = true;
   }

   @Override
   protected void run(CommandCallback<List<TransactionModel>> callback) throws Throwable {
      if (readFromCache) {
         callback.onSuccess(cachedItems);
      } else {
         if (cachedItems != null && cachedItems.isEmpty()) {
            callback.onProgress(0);
         }
         janet.createPipe(GetTransactionsHttpAction.class)
               .createObservableResult(new GetTransactionsHttpAction(reviewParams.take(), reviewParams.skip(), reviewParams
                     .excludeInAppPaymentStatusInitiated(),
                     reviewParams.localeId(), sessionHolder.get().get().username(),
                     sessionHolder.get().get().legacyApiToken()))
               .map(GetTransactionsHttpAction::getResponse)
               .map(ThrstTransactionResponse::results)
               .map(detailTransactionThrsts -> mapperyContext.convert(detailTransactionThrsts, TransactionModel.class))
               .subscribe(callback::onSuccess, callback::onFail);
      }
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.dtl_load_error;
   }

   @Override
   public List<TransactionModel> getCacheData() {
      return new ArrayList<>(getResult());
   }

   @Override
   public void onRestore(ActionHolder holder, List<TransactionModel> cache) {
      cachedItems = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundle bundle = new CacheBundleImpl();
      bundle.put(PaginatedStorage.BUNDLE_REFRESH, refresh);
      return new CacheOptions(readFromCache, saveToCache, true, bundle);
   }
}
