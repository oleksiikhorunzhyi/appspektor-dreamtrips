package com.worldventures.dreamtrips.modules.dtl_flow.di;


import com.worldventures.dreamtrips.modules.dtl.view.cell.delegates.MerchantsAdapterDelegate;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class DtlDelegatesModule {

   @Provides()
   MerchantsAdapterDelegate provideMerchantsAdapterDelegate() {
      return new MerchantsAdapterDelegate();
   }
}
