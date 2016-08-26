package com.worldventures.dreamtrips.modules.mapping;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.mapping.converter.ShortProfilesConverter;
import com.worldventures.dreamtrips.modules.mapping.mapper.FlagsMapper;
import com.worldventures.dreamtrips.modules.mapping.mapper.PodcastsMapper;
import com.worldventures.dreamtrips.modules.mapping.mapper.ShortProfilesMapper;

import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.mappery.Mappery;

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

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideShortProfilesConverter() {
      return new ShortProfilesConverter();
   }

   @Provides
   @Singleton
   Mappery provideMappery(Set<Converter> converters) {
      Mappery.Builder builder = new Mappery.Builder();
      for (Converter converter : converters) {
         builder.map(converter.sourceClass()).to(converter.targetClass(), converter);
      }
      return builder.build();
   }

   @Provides
   @Singleton
   PodcastsMapper providePodcastsMapper() {
      return new PodcastsMapper();  
   }
}
