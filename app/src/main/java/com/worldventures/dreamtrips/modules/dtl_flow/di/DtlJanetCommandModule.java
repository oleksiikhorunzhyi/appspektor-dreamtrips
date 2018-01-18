package com.worldventures.dreamtrips.modules.dtl_flow.di;

import com.google.gson.TypeAdapterFactory;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantDetailsViewCommand;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.transactions.GsonAdaptersDetailTransactionThrst;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.transactions.GsonAdaptersThrstTransactionResponse;
import com.worldventures.dreamtrips.modules.dtl.service.action.AddReviewAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.AttributesAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.ClearMerchantsStorageAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.FlaggingReviewAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.FullMerchantAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.GetTransactionsCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.NearbyLocationAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.ReviewMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.SearchLocationAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.TransactionPilotAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.UploadReceiptCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.UrlTokenAction;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            DtlTransactionAction.class,
            SearchLocationAction.class,
            DtlAnalyticsCommand.class,
            MerchantDetailsViewCommand.class,
            AttributesAction.class,
            FullMerchantAction.class,
            ClearMerchantsStorageAction.class,
            MerchantsAction.class,
            NearbyLocationAction.class,
            ReviewMerchantsAction.class,
            AddReviewAction.class,
            FlaggingReviewAction.class,
            UrlTokenAction.class,
            TransactionPilotAction.class,
            GetTransactionsCommand.class,
            UploadReceiptCommand.class
      },
      complete = false, library = true)
public class DtlJanetCommandModule {

   @Provides(type = Provides.Type.SET)
   TypeAdapterFactory gsonAdaptersDetailTransactionThrst() {
      return new GsonAdaptersDetailTransactionThrst();
   }

   @Provides(type = Provides.Type.SET)
   TypeAdapterFactory gsonAdaptersThrstTransactionResponse() {
      return new GsonAdaptersThrstTransactionResponse();
   }
}
