package com.worldventures.dreamtrips.wallet.di;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandlerFactory;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class WalletUtilModule {

   @Provides
   ErrorHandlerFactory provideErrerHandlerFactory(@ForApplication Context context, HttpErrorHandlingUtil errorHandlingUtils) {
      return new ErrorHandlerFactory(context, errorHandlingUtils);
   }


}
