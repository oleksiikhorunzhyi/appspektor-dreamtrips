package com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlDetailPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar.MasterToolbarPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;

import flow.path.Path;

@Layout(R.layout.activity_detail_transaction)
public class DtlTransactionPath extends DtlDetailPath {
   private TransactionModel transaction;

   public DtlTransactionPath(MasterDetailPath path, TransactionModel transaction) {
      super(path);
      this.transaction = transaction;
   }

   @Override
   public PathAttrs getAttrs() {
      return PathAttrs.WITHOUT_DRAWER;
   }

   @Override
   public Path getMasterToolbarPath() {
      return MasterToolbarPath.INSTANCE;
   }

   public TransactionModel getTransaction() {
      return transaction;
   }

   public void setTransaction(TransactionModel transaction) {
      this.transaction = transaction;
   }
}
