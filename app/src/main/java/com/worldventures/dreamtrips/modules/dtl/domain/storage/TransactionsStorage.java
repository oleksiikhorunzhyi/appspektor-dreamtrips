package com.worldventures.dreamtrips.modules.dtl.domain.storage;

import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedMemoryStorage;
import com.worldventures.dreamtrips.modules.dtl.service.action.GetTransactionsCommand;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;

import java.util.List;

public class TransactionsStorage extends PaginatedMemoryStorage<TransactionModel> implements ActionStorage<List<TransactionModel>> {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetTransactionsCommand.class;
   }
}
