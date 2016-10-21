package com.worldventures.dreamtrips.modules.dtl_flow.di;

import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantDetailsViewCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.AttributesAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.ClearMerchantsStorageAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlNearbyLocationAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlSearchLocationAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.FullMerchantAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantsAction;

import dagger.Module;

@Module(
      injects = {
            DtlTransactionAction.class,
            DtlSearchLocationAction.class,
            DtlAnalyticsCommand.class,
            MerchantDetailsViewCommand.class,
            AttributesAction.class,
            FullMerchantAction.class,
            ClearMerchantsStorageAction.class,
            MerchantsAction.class,
            DtlNearbyLocationAction.class,
      },
      complete = false, library = true)
public class DtlJanetCommandModule {
}
