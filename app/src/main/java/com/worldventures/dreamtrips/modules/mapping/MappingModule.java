package com.worldventures.dreamtrips.modules.mapping;

import com.worldventures.dreamtrips.modules.mapping.mapper.FlagsMapper;
import com.worldventures.dreamtrips.modules.mapping.mapper.ShortProfilesMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {},
      library = true, complete = false)
public class MappingModule {

   @Provides
   @Singleton
   FlagsMapper provideFlagsMapper() {
      return new FlagsMapper();
   }

   @Provides
   @Singleton
   ShortProfilesMapper provideShortProfilesMapper() {
      return new ShortProfilesMapper();
   }
}
