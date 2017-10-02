package com.worldventures.core.modules.picker.presenter.facebook.albums;


import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.modules.facebook.FacebookHelper;
import com.worldventures.core.modules.facebook.exception.FacebookAccessTokenException;
import com.worldventures.core.modules.facebook.service.FacebookInteractor;
import com.worldventures.core.modules.facebook.service.command.GetAlbumsCommand;
import com.worldventures.core.modules.picker.viewmodel.FacebookAlbumPickerViewModel;
import com.worldventures.core.modules.picker.presenter.facebook.FacebookMediaPickerPresenterImpl;
import com.worldventures.core.modules.picker.service.MediaPickerFacebookService;
import com.worldventures.core.modules.picker.view.facebook.albums.FacebookAlbumPickerView;

import java.util.List;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class FacebookAlbumsPickerPresenterImpl extends FacebookMediaPickerPresenterImpl<FacebookAlbumPickerView, FacebookAlbumPickerViewModel> implements FacebookAlbumsPickerPresenter {

   private final FacebookHelper facebookHelper;
   private final MediaPickerFacebookService mediaPickerFacebookService;

   public FacebookAlbumsPickerPresenterImpl(FacebookHelper facebookHelper, MediaPickerFacebookService mediaPickerFacebookService, FacebookInteractor facebookInteractor) {
      super(facebookInteractor);
      this.facebookHelper = facebookHelper;
      this.mediaPickerFacebookService = mediaPickerFacebookService;
   }

   @Override
   public void attachView(FacebookAlbumPickerView view) {
      super.attachView(view);
      if (!facebookHelper.isLoggedIn()) {
         loginToFb();
      } else {
         loadItems();
      }
   }

   private void loginToFb() {
      mediaPickerFacebookService
            .checkFacebookLogin(FacebookHelper.LOGIN_PERMISSIONS)
            .compose(getView().lifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(notification ->  {
               if (notification.isOnNext()) {
                  loadItems();
               } else if (notification.isOnCompleted()) {
                  getView().goBack();
               } else {
                  Timber.e(notification.getThrowable(), "Cannot perform login");
               }
            });
   }

   @Override
   public void observeItemSource() {
      getFacebookInteractor()
            .albumsPipe()
            .observe()
            .compose(getView().lifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationGetAlbums())
                  .onSuccess(getAlbumsCommand -> {
                     List<FacebookAlbumPickerViewModel> albumList = Queryable
                           .from(getAlbumsCommand.getResult())
                           .filter(album -> album.getCount() > 0)
                           .map(facebookAlbum -> new FacebookAlbumPickerViewModel(facebookAlbum.getId(),
                                 facebookAlbum.getName(),
                                 facebookAlbum.getCount(),
                                 facebookAlbum.getCoverPhoto()))
                           .toList();
                     getView().addItems(albumList);
                  })
                  .onFail((getAlbumsCommand, throwable) -> {
                     Timber.e(throwable, "Cannot load albums");
                     final Throwable causeThrowable = throwable.getCause();
                     if (causeThrowable instanceof FacebookAccessTokenException) {
                        loginToFb();
                     }
                  })
                  .create());
   }

   @Override
   public void loadItems() {
      getFacebookInteractor().albumsPipe().send(GetAlbumsCommand.refresh());
   }

   @Override
   public void loadMore() {
      getFacebookInteractor().albumsPipe().send(GetAlbumsCommand.loadMore());
   }
}
