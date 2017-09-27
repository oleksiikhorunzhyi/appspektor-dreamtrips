package com.worldventures.dreamtrips.modules.mapping;

import com.worldventures.dreamtrips.modules.mapping.converter.AccountToUserConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.mapping.converter.FeatureConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.SessionConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.SettingConverter;

import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.mappery.Mappery;
import io.techery.mappery.MapperyContext;
import timber.log.Timber;

@Module(library = true, complete = false)
public class MappingModule {

   @Provides
   @Singleton
   MapperyContext provideMappery(Set<Converter> converters) {
      Mappery.Builder builder = new Mappery.Builder();
      for (Converter converter : converters) {
         if (converter.sourceClass() != null && converter.targetClass() != null) {
            builder.map(converter.sourceClass()).to(converter.targetClass(), converter);
         } else {
            Timber.w("sourceClass or targetClass is null, converter %s will be ignored",
                  converter.getClass().getName());
         }
      }
      return builder.build();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideSessionConverter() {
      return new SessionConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideSettingConverter() {
      return new SettingConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideFeatureConverter() {
      return new FeatureConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideAccountToUserConverter() {
      return new AccountToUserConverter();
   }
}
