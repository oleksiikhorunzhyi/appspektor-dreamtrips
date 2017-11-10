package com.worldventures.dreamtrips.modules.dtl.service.action;


import android.support.v4.util.Pair;

import com.worldventures.core.janet.cache.CacheOptions;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

@CommandAction
public class DtlTransactionAction extends Command<DtlTransaction> implements CachedAction<Pair<String, DtlTransaction>>, InjectableAction {

   @Inject SnappyRepository db;

   private final String id;
   private Func1<DtlTransaction, DtlTransaction> updateFunc;
   private DtlTransaction transaction;


   private DtlTransactionAction(Merchant merchant, Func1<DtlTransaction, DtlTransaction> updateFunc) {
      this.id = merchant.id();
      this.updateFunc = updateFunc;
   }

   private DtlTransactionAction(String id, Func1<DtlTransaction, DtlTransaction> updateFunc) {
      this.id = id;
      this.updateFunc = updateFunc;
   }

   public static DtlTransactionAction get(String id) {
      return new DtlTransactionAction(id, null);
   }

   public static DtlTransactionAction get(Merchant merchant) {
      return new DtlTransactionAction(merchant, null);
   }

   public static DtlTransactionAction update(Merchant merchant, Func1<DtlTransaction, DtlTransaction> updateFunc) {
      return new DtlTransactionAction(merchant, updateFunc);
   }

   public static DtlTransactionAction save(Merchant merchant, DtlTransaction transaction) {
      return update(merchant, old -> transaction);
   }

   public static DtlTransactionAction clean(Merchant merchant) {
      return update(merchant, transaction -> ImmutableDtlTransaction.copyOf(transaction)
            .withUploadTask(null)
            .withBillTotal(0d)
            .withReceiptPhotoUrl(null)
            .withMerchantToken(null)
            .withIsVerified(false)
            .withDtlTransactionResult(null)
            .withPoints(0d));
   }

   public static DtlTransactionAction delete(Merchant merchant) {
      return update(merchant, transaction -> null);
   }

   @Override
   protected void run(CommandCallback<DtlTransaction> callback) throws Throwable {
      if (transaction == null) transaction = db.getDtlTransaction(id);
      //changing
      if (updateFunc != null) {
         transaction = updateFunc.call(transaction);
         if (transaction != null) {
            db.saveDtlTransaction(id, transaction);
         } else {
            db.deleteDtlTransaction(id);
         }
      }
      callback.onSuccess(transaction);
   }

   @Override
   public Pair<String, DtlTransaction> getCacheData() {
      return new Pair<>(id, transaction);
   }

   @Override
   public void onRestore(ActionHolder holder, Pair<String, DtlTransaction> cache) {
      if (cache.first.equals(id)) {
         this.transaction = cache.second;
      }
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder().build();
   }
}
