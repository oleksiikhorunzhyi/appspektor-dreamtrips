package com.worldventures.dreamtrips.core.module;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.dreamtrips.core.navigation.creator.BucketDetailsFragmentClassProvider;
import com.worldventures.dreamtrips.core.navigation.creator.ProfileFragmentClassProvider;
import com.worldventures.dreamtrips.core.navigation.creator.FragmentClassProvider;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlTransactionFragmentClassProvider;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(
      complete = false,
      library = true)
public class FragmentClassProviderModule {
   public static final String PROFILE = "profile";
   public static final String BUCKET_DETAILS = "bucket_details";
   public static final String DTL_TRANSACTION = "dtl_transaction";

   @Provides
   @Named(PROFILE)
   FragmentClassProvider<Integer> provideProfileRouteCreator(SessionHolder appSessionHolder) {
      return new ProfileFragmentClassProvider(appSessionHolder);
   }

   @Provides
   @Named(BUCKET_DETAILS)
   FragmentClassProvider<Integer> provideBucketDetailsRouteCreator(SessionHolder appSessionHolder) {
      return new BucketDetailsFragmentClassProvider(appSessionHolder);
   }

   @Provides
   @Named(DTL_TRANSACTION)
   FragmentClassProvider<DtlTransaction> provideDtlTransactionRouteCreator() {
      return new DtlTransactionFragmentClassProvider();
   }
}
