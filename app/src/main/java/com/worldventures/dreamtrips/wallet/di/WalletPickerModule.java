package com.worldventures.dreamtrips.wallet.di;

import android.app.Activity;

import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.facebook.FacebookHelper;
import com.worldventures.dreamtrips.modules.facebook.service.FacebookInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;
import com.worldventures.dreamtrips.wallet.service.picker.WalletPickerFacebookService;
import com.worldventures.dreamtrips.wallet.service.picker.WalletPickerFacebookServiceImpl;
import com.worldventures.dreamtrips.wallet.ui.common.picker.dialog.WalletPickerDialog;
import com.worldventures.dreamtrips.wallet.ui.common.picker.dialog.WalletPickerDialogPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.picker.dialog.WalletPickerDialogPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.albums.WalletPickerFacebookAlbumsLayout;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.albums.WalletPickerFacebookAlbumsPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.albums.WalletPickerFacebookAlbumsPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.photos.WalletPickerFacebookPhotosLayout;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.photos.WalletPickerFacebookPhotosPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.photos.WalletPickerFacebookPhotosPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.picker.gallery.WalletGalleryPickerLayout;
import com.worldventures.dreamtrips.wallet.ui.common.picker.gallery.WalletGalleryPickerPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.picker.gallery.WalletGalleryPickerPresenterImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {},
        injects = {
            WalletPickerDialog.class,
            WalletGalleryPickerLayout.class,
            WalletPickerFacebookAlbumsLayout.class,
            WalletPickerFacebookPhotosLayout.class,
        },
        complete = false, library = true)
public class WalletPickerModule {

   @Provides
   @Singleton
   WalletPickerFacebookService provideWalletPickerFacebookService(Activity activity) {
      return new WalletPickerFacebookServiceImpl(activity);
   }

   @Provides
   WalletPickerDialogPresenter provideWalletPickerDialogPresenter() {
      return new WalletPickerDialogPresenterImpl();
   }

   @Provides
   WalletGalleryPickerPresenter provideWalletGalleryPickerPresenter(PickImageDelegate pickImageDelegate,
         MediaInteractor mediaInteractor, PermissionDispatcher permissionDispatcher) {
      return new WalletGalleryPickerPresenterImpl(pickImageDelegate, mediaInteractor, permissionDispatcher);
   }

   @Provides
   WalletPickerFacebookAlbumsPresenter provideWalletPickerFacebookAlbumsPresenter(FacebookHelper facebookHelper, FacebookInteractor facebookInteractor,
         WalletPickerFacebookService walletPickerFacebookService) {
      return new WalletPickerFacebookAlbumsPresenterImpl(facebookHelper, walletPickerFacebookService, facebookInteractor);
   }

   @Provides
   WalletPickerFacebookPhotosPresenter provideWalletPickerFacebookPhotosPresenter(FacebookInteractor facebookInteractor) {
      return new WalletPickerFacebookPhotosPresenterImpl(facebookInteractor);
   }
}
