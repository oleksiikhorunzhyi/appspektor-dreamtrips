package com.worldventures.dreamtrips.modules.facebook;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookAlbumPresenter;
import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookPhotoPresenter;
import com.worldventures.dreamtrips.modules.facebook.service.command.GetAlbumsCommand;
import com.worldventures.dreamtrips.modules.facebook.view.cell.FacebookAlbumCell;
import com.worldventures.dreamtrips.modules.facebook.view.cell.FacebookPhotoCell;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookAlbumFragment;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookPhotoFragment;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {FacebookAlbumPresenter.class,
            FacebookPhotoPresenter.class,
            FacebookAlbumFragment.class,
            FacebookPhotoFragment.class,
            FacebookAlbumCell.class,
            FacebookPhotoCell.class
      },
      complete = false,
      library = true)
public class FacebookModule {

   public static final String FACEBOOK_GSON_QUALIFIER = "FACEBOOK_GSON_QUALIFIER";

   @Named(FACEBOOK_GSON_QUALIFIER)
   @Singleton
   @Provides
   Gson provideGson() {
      return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
   }

   @Singleton
   @Provides
   FacebookHelper provideFacebookHelper(@Named(FacebookModule.FACEBOOK_GSON_QUALIFIER) Gson gson) {
       return new FacebookHelper(gson);
   }
}
