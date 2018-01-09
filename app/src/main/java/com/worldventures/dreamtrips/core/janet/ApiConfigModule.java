package com.worldventures.dreamtrips.core.janet;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.utils.AppVersionNameBuilder;
import com.worldventures.dreamtrips.api.api_common.service.MonolithConfigData;
import com.worldventures.dreamtrips.core.janet.api_lib.MonolithAuthData;
import com.worldventures.dreamtrips.core.janet.api_lib.MonolithAuthDataProvider;
import com.worldventures.dreamtrips.core.janet.api_lib.MonolithConfigDataProvider;
import com.worldventures.dreamtrips.mobilesdk.AuthProviders;
import com.worldventures.dreamtrips.mobilesdk.ConfigProviders;
import com.worldventures.dreamtrips.mobilesdk.ImmutableAuthProviders;
import com.worldventures.dreamtrips.mobilesdk.ImmutableConfigProviders;
import com.worldventures.dreamtrips.mobilesdk.authentication.AuthDataProvider;
import com.worldventures.dreamtrips.mobilesdk.config.ConfigDataProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class ApiConfigModule {

   @Singleton
   @Provides
   AuthProviders provideAuthProviders(AuthDataProvider<MonolithAuthData> monolithAuthDataProvider) {
      return ImmutableAuthProviders.builder()
            .monolithAuth(monolithAuthDataProvider)
            .build();
   }

   @Singleton
   @Provides
   AuthDataProvider<MonolithAuthData> provideMonolithAuthDataProvider(SessionHolder sessionHolder) {
      return new MonolithAuthDataProvider(sessionHolder);
   }

   @Singleton
   @Provides
   ConfigDataProvider<MonolithConfigData> provideMonolithConfigDataProvider(AppVersionNameBuilder versionNameBuilder) {
      return new MonolithConfigDataProvider(versionNameBuilder);
   }

   @Singleton
   @Provides
   ConfigProviders provideConfigProviders(ConfigDataProvider<MonolithConfigData> monolithConfigDataProvider) {
      return ImmutableConfigProviders.builder()
            .monolithConfig(monolithConfigDataProvider)
            .build();
   }

}
