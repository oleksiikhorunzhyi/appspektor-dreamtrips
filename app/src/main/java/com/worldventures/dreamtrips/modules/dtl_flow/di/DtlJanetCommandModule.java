package com.worldventures.dreamtrips.modules.dtl_flow.di;

import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantDetailsViewCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.AttributesAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlSearchLocationAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantByIdCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.ThinMerchantsCommand;

import dagger.Module;

@Module(
      injects = {
            DtlTransactionAction.class,
            DtlSearchLocationAction.class,
            DtlAnalyticsCommand.class,
            MerchantDetailsViewCommand.class,
            AttributesAction.class,
            MerchantByIdCommand.class,
            ThinMerchantsCommand.class
      },
      complete = false, library = true)
public class DtlJanetCommandModule {
}
