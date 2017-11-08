package com.worldventures.dreamtrips.modules.dtl.service.action.http;

import com.worldventures.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.transactions.ThrstTransactionResponse;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.http.BaseThirdPartyHttpAction;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.RequestHeader;
import io.techery.janet.http.annotations.Response;
import io.techery.janet.http.annotations.Url;

@HttpAction
public class GetTransactionsHttpAction extends BaseThirdPartyHttpAction {

   @Url final String url = BuildConfig.TRANSACTIONS_API_URL.concat("merchants/transactions");

   @RequestHeader("Authorization") String header;

   @Query("skip") String skip;
   @Query("take") String take;
   @Query("ExcludeInAppPaymentStatusInitiated") Boolean excludeInAppPaymentStatusInitiated;
   @Query("localeId") String localeId;
   @Query("api-version") final String apiVersion = "2.0";

   @Response ThrstTransactionResponse response;

   public GetTransactionsHttpAction(int take, int skip, boolean excludeInAppPaymentStatusInitiated, String localeId, String userId, String ssoToken) {
      this.header = "Basic " + ProjectTextUtils.convertToBase64NoWrap(userId + ":" + ssoToken);
      this.skip = String.valueOf(skip);
      this.take = String.valueOf(take);
      this.excludeInAppPaymentStatusInitiated = excludeInAppPaymentStatusInitiated;
      this.localeId = localeId;
   }

   public ThrstTransactionResponse getResponse() {
      return response;
   }
}
