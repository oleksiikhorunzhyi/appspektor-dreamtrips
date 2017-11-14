package com.worldventures.dreamtrips.modules.dtl.helper;

import android.app.Activity;
import android.support.v4.app.FragmentManager;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.creator.FragmentClassProvider;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantBundle;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;

public class DtlEnrollWizard {

   private final Router router;
   private final FragmentClassProvider<DtlTransaction> fragmentClassProvider;

   public DtlEnrollWizard(Router router, FragmentClassProvider<DtlTransaction> fragmentClassProvider) {
      this.router = router;
      this.fragmentClassProvider = fragmentClassProvider;
   }

   public void clearAndProceed(FragmentManager fragmentManager, DtlTransaction dtlTransaction, MerchantBundle bundle) {
      showNext(fragmentManager, dtlTransaction, bundle, true);
   }

   public void proceed(FragmentManager fragmentManager, DtlTransaction dtlTransaction, MerchantBundle bundle) {
      showNext(fragmentManager, dtlTransaction, bundle, false);
   }

   private void showNext(FragmentManager fragmentManager, DtlTransaction dtlTransaction, MerchantBundle bundle, boolean clearBackStack) {
      router.moveTo(fragmentClassProvider.provideFragmentClass(dtlTransaction), NavigationConfigBuilder.forFragment()
            .containerId(R.id.container_main)
            .backStackEnabled(true)
            .data(bundle)
            .clearBackStack(clearBackStack)
            .fragmentManager(fragmentManager)
            .build());
   }

   public void finish(Activity activity) {
      activity.finish();
   }
}
