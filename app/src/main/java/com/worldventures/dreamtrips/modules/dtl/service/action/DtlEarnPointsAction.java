package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransactionResult;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/dtl/v2/merchants/{id}/transactions", method = HttpAction.Method.POST)
public class DtlEarnPointsAction extends AuthorizedHttpAction {

   @Path("id") String merchantId;

   @Body DtlTransaction.Request request;

   @Response DtlTransactionResult result;

   private final DtlTransaction transaction;
   private final Merchant merchant;

   public DtlEarnPointsAction(Merchant merchant, DtlTransaction transaction) {
      this.merchant = merchant;
      this.transaction = transaction;
      this.merchantId = merchant.id();
      this.request = transaction.asTransactionRequest(merchant.asMerchantAttributes().defaultCurrency().code());
   }

   public DtlTransaction getTransaction() {
      return transaction;
   }

   public Merchant getMerchant() {
      return merchant;
   }

   public DtlTransactionResult getResult() {
      return result;
   }
}
