package com.worldventures.dreamtrips.modules.dtl.domain.storage;

import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.transactions.DetailTransactionThrst;
import com.worldventures.dreamtrips.modules.dtl.service.action.GetTransactionsCommand;

import java.util.List;

public class TransactionsStorage extends MemoryStorage<List<DetailTransactionThrst>> implements ActionStorage<List<DetailTransactionThrst>> {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetTransactionsCommand.class;
   }
}
