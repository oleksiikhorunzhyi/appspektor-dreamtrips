package com.worldventures.dreamtrips.modules.picker;

import android.app.Activity;

import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.facebook.FacebookHelper;
import com.worldventures.dreamtrips.modules.facebook.service.FacebookInteractor;
import com.worldventures.dreamtrips.modules.picker.presenter.dialog.MediaPickerDialogPresenter;
import com.worldventures.dreamtrips.modules.picker.presenter.dialog.MediaPickerDialogPresenterImpl;
import com.worldventures.dreamtrips.modules.picker.presenter.facebook.albums.FacebookAlbumsPickerPresenter;
import com.worldventures.dreamtrips.modules.picker.presenter.facebook.albums.FacebookAlbumsPickerPresenterImpl;
import com.worldventures.dreamtrips.modules.picker.presenter.facebook.photos.FacebookPhotosPickerPresenter;
import com.worldventures.dreamtrips.modules.picker.presenter.facebook.photos.FacebookPhotosPickerPresenterImpl;
import com.worldventures.dreamtrips.modules.picker.presenter.gallery.GalleryMediaPickerPresenter;
import com.worldventures.dreamtrips.modules.picker.presenter.gallery.GalleryMediaPickerPresenterImpl;
import com.worldventures.dreamtrips.modules.picker.view.dialog.MediaPickerDialog;
import com.worldventures.dreamtrips.modules.picker.view.facebook.albums.FacebookAlbumsPickerLayout;
import com.worldventures.dreamtrips.modules.picker.view.facebook.photos.FacebookPhotosPickerLayout;
import com.worldventures.dreamtrips.modules.picker.view.gallery.GalleryMediaPickerLayout;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;
import com.worldventures.dreamtrips.modules.picker.service.MediaPickerFacebookService;
import com.worldventures.dreamtrips.modules.picker.service.MediaPickerFacebookServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {},
        injects = {
            MediaPickerDialog.class,
            GalleryMediaPickerLayout.class,
            FacebookAlbumsPickerLayout.class,
            FacebookPhotosPickerLayout.class,
        },
        complete = false, library = true)
public class MediaPickerModule {

   @Provides
   @Singleton
   MediaPickerFacebookService provideMediaPickerFacebookService(Activity activity) {
      return new MediaPickerFacebookServiceImpl(activity);
   }

   @Provides
   MediaPickerDialogPresenter provideMediaPickerDialogPresenter() {
      return new MediaPickerDialogPresenterImpl();
   }

   @Provides
   GalleryMediaPickerPresenter provideGalleryMediaPickerPresenter(PickImageDelegate pickImageDelegate,
         MediaInteractor mediaInteractor, PermissionDispatcher permissionDispatcher) {
      return new GalleryMediaPickerPresenterImpl(pickImageDelegate, mediaInteractor, permissionDispatcher);
   }

   @Provides
   FacebookAlbumsPickerPresenter provideFacebookAlbumsPickerPresenter(FacebookHelper facebookHelper, FacebookInteractor facebookInteractor,
         MediaPickerFacebookService mediaPickerFacebookService) {
      return new FacebookAlbumsPickerPresenterImpl(facebookHelper, mediaPickerFacebookService, facebookInteractor);
   }

   @Provides
   FacebookPhotosPickerPresenter provideFacebookPhotosPickerPresenter(FacebookInteractor facebookInteractor) {
      return new FacebookPhotosPickerPresenterImpl(facebookInteractor);
   }
}
