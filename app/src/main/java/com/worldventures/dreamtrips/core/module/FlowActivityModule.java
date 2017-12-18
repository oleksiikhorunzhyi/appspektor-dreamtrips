package com.worldventures.dreamtrips.core.module;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.techery.spares.utils.gson.LowercaseEnumTypeAdapterFactory;
import com.worldventures.core.utils.DateTimeDeserializer;
import com.worldventures.core.utils.DateTimeSerializer;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;

import org.immutables.gson.adapter.util.ImmutablesGsonAdaptersProvider;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

import static dagger.Provides.Type.SET_VALUES;

@Module(
      injects = {FlowActivity.class},
      library = true, complete = false)
public class FlowActivityModule {

   public static final String LABEL = "FlowActivityModule";

   @Provides
   @Named(LABEL)
   Gson provideGson(@Named(LABEL) Set<TypeAdapterFactory> adapterFactories) {
      GsonBuilder builder = new GsonBuilder().serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory("unknown"))
            .registerTypeAdapter(Date.class, new DateTimeDeserializer())
            .registerTypeAdapter(Date.class, new DateTimeSerializer());
      for (TypeAdapterFactory factory : adapterFactories) {
         builder.registerTypeAdapterFactory(factory);
      }
      return builder.create();
   }

   @Named(FlowActivityModule.LABEL)
   @Provides(type = SET_VALUES)
   Set<TypeAdapterFactory> provideImmutablesFactory() {
      return new HashSet<>(new ImmutablesGsonAdaptersProvider().getAdapters());
   }

}
