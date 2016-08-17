package com.worldventures.dreamtrips.core.module;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.navigation.creator.BucketDetailsRouteCreator;
import com.worldventures.dreamtrips.core.navigation.creator.ProfileRouteCreator;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlTransactionRouteCreator;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {ProfileRouteCreator.class},
      complete = false,
      library = true)
public class RouteCreatorModule {
   public static final String PROFILE = "profile";
   public static final String BUCKET_DETAILS = "bucket_details";
   public static final String DTL_TRANSACTION = "dtl_transaction";

   @Provides
   @Named(PROFILE)
   RouteCreator<Integer> provideProfileRouteCreator(SessionHolder<UserSession> appSessionHolder) {
      return new ProfileRouteCreator(appSessionHolder);
   }

   @Provides
   @Named(BUCKET_DETAILS)
   RouteCreator<Integer> provideBucketDetailsRouteCreator(SessionHolder<UserSession> appSessionHolder) {
      return new BucketDetailsRouteCreator(appSessionHolder);
   }

   @Provides
   @Named(DTL_TRANSACTION)
   RouteCreator<DtlTransaction> provideDtlTransactionRouteCreator() {
      return new DtlTransactionRouteCreator();
   }
}
