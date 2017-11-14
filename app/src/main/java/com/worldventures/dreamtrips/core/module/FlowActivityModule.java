package com.worldventures.dreamtrips.core.module;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.techery.spares.utils.gson.LowercaseEnumTypeAdapterFactory;
import com.worldventures.core.utils.DateTimeDeserializer;
import com.worldventures.core.utils.DateTimeSerializer;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;

import java.util.Date;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {FlowActivity.class},
      library = true, complete = false)
public class FlowActivityModule {

   public static final String LABEL = "FlowActivityModule";

   @Provides
   @Named(LABEL)
   Gson provideGson() {
      return new GsonBuilder().serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory("unknown"))
            .registerTypeAdapter(Date.class, new DateTimeDeserializer())
            .registerTypeAdapter(Date.class, new DateTimeSerializer())
            .create();
   }

}
