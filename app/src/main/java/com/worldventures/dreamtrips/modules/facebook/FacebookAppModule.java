package com.worldventures.dreamtrips.modules.facebook;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.worldventures.dreamtrips.modules.facebook.service.command.GetAlbumsCommand;
import com.worldventures.dreamtrips.modules.facebook.service.command.GetPhotosCommand;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
         GetAlbumsCommand.class,
         GetPhotosCommand.class
      },
      complete = false,
      library = true)
public class FacebookAppModule {

   public static final String FACEBOOK_GSON_QUALIFIER = "FACEBOOK_GSON_QUALIFIER";

   @Named(FACEBOOK_GSON_QUALIFIER)
   @Singleton
   @Provides
   Gson provideGson() {
      return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
   }

   @Singleton
   @Provides
   FacebookHelper provideFacebookHelper(@Named(FacebookAppModule.FACEBOOK_GSON_QUALIFIER) Gson gson) {
       return new FacebookHelper(gson);
   }
}
