package com.worldventures.dreamtrips.modules.dtl_flow.di;

import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.MerchantDetailsViewCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.AttributesAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantByIdAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlSearchLocationAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlUpdateAmenitiesAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantByIdCommand;

import dagger.Module;

@Module(
      injects = {
            DtlUpdateAmenitiesAction.class,
            DtlFilterDataAction.class,
            DtlTransactionAction.class,
            DtlMerchantByIdAction.class,
            DtlMerchantsAction.class,
            DtlSearchLocationAction.class,
            DtlAnalyticsCommand.class,
            MerchantDetailsViewCommand.class,
            AttributesAction.class,
            DtlLocationCommand.class,
            MerchantByIdCommand.class
      },
      complete = false, library = true)
public class DtlJanetCommandModule {
}
