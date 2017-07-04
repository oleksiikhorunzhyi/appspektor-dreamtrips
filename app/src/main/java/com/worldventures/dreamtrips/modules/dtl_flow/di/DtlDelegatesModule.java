package com.worldventures.dreamtrips.modules.dtl_flow.di;


import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.modules.dtl.view.cell.delegates.MerchantsAdapterDelegate;
import com.worldventures.dreamtrips.modules.dtl.view.util.DtlApiErrorViewAdapter;
import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class DtlDelegatesModule {

   @Provides
   MerchantsAdapterDelegate provideMerchantsAdapterDelegate() {
      return new MerchantsAdapterDelegate();
   }

   @Provides
   DtlApiErrorViewAdapter provideApiErrorViewAdapter(@ForApplication Context context, HttpErrorHandlingUtil errorHandlingUtils) {
      return new DtlApiErrorViewAdapter(context, errorHandlingUtils);
   }
}
