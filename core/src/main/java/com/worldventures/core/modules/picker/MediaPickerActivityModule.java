package com.worldventures.core.modules.picker;


import android.app.Activity;

import com.worldventures.core.modules.facebook.FacebookHelper;
import com.worldventures.core.modules.facebook.service.FacebookInteractor;
import com.worldventures.core.modules.picker.presenter.dialog.MediaPickerDialogPresenter;
import com.worldventures.core.modules.picker.presenter.dialog.MediaPickerDialogPresenterImpl;
import com.worldventures.core.modules.picker.presenter.facebook.albums.FacebookAlbumsPickerPresenter;
import com.worldventures.core.modules.picker.presenter.facebook.albums.FacebookAlbumsPickerPresenterImpl;
import com.worldventures.core.modules.picker.presenter.facebook.photos.FacebookPhotosPickerPresenter;
import com.worldventures.core.modules.picker.presenter.facebook.photos.FacebookPhotosPickerPresenterImpl;
import com.worldventures.core.modules.picker.presenter.gallery.GalleryMediaPickerPresenter;
import com.worldventures.core.modules.picker.presenter.gallery.GalleryMediaPickerPresenterImpl;
import com.worldventures.core.modules.picker.service.MediaPickerFacebookService;
import com.worldventures.core.modules.picker.service.MediaPickerFacebookServiceImpl;
import com.worldventures.core.modules.picker.service.MediaPickerInteractor;
import com.worldventures.core.modules.picker.service.PickImageDelegate;
import com.worldventures.core.modules.picker.util.MediaCapturingRouter;
import com.worldventures.core.modules.picker.view.dialog.MediaPickerDialog;
import com.worldventures.core.modules.picker.view.facebook.albums.FacebookAlbumsPickerLayout;
import com.worldventures.core.modules.picker.view.facebook.photos.FacebookPhotosPickerLayout;
import com.worldventures.core.modules.picker.view.gallery.GalleryMediaPickerLayout;
import com.worldventures.core.ui.util.permission.PermissionDispatcher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            MediaPickerDialog.class,
            GalleryMediaPickerLayout.class,
            FacebookAlbumsPickerLayout.class,
            FacebookPhotosPickerLayout.class,
      },
      includes = {
            MediaPickerAppModule.class,
      },
      complete = false, library = true)
public class MediaPickerActivityModule {

   @Provides
   MediaCapturingRouter provideMediaCapturingRouter(Activity activity) {
      return new MediaCapturingRouter(activity);
   }

   @Provides
   @Singleton
   PickImageDelegate pickImageDelegate(MediaCapturingRouter mediaCapturingRouter, MediaPickerInteractor mediaPickerInteractor) {
      return new PickImageDelegate(mediaCapturingRouter, mediaPickerInteractor);
   }

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
         MediaPickerInteractor mediaPickerInteractor, PermissionDispatcher permissionDispatcher) {
      return new GalleryMediaPickerPresenterImpl(pickImageDelegate, mediaPickerInteractor, permissionDispatcher);
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
