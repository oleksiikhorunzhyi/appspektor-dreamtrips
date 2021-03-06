package com.worldventures.dreamtrips.modules.dtl_flow.di;

import android.content.Context;

import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.modules.dtl.domain.storage.snappy.DtlSnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.helper.howtopayvideo.HowToPayHintDelegate;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlScanReceiptErrorAdapter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.delegates.MerchantsAdapterDelegate;
import com.worldventures.dreamtrips.modules.dtl.view.util.DtlApiErrorViewAdapter;

import javax.inject.Singleton;

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

   @Provides
   DtlScanReceiptErrorAdapter provideScanReceiptErrorViewAdapter(@ForApplication Context context, HttpErrorHandlingUtil errorHandlingUtils) {
      return new DtlScanReceiptErrorAdapter(context, errorHandlingUtils);
   }

   @Provides
   @Singleton
   HowToPayHintDelegate provideHowToPayHintDelegate(DtlSnappyRepository dtlSnappyRepository) {
      return new HowToPayHintDelegate(dtlSnappyRepository);
   }
}
