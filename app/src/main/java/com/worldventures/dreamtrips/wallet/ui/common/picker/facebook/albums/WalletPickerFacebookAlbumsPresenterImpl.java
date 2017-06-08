package com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.albums;


import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.facebook.FacebookHelper;
import com.worldventures.dreamtrips.modules.facebook.exception.FacebookAccessTokenException;
import com.worldventures.dreamtrips.modules.facebook.service.FacebookInteractor;
import com.worldventures.dreamtrips.modules.facebook.service.command.GetAlbumsCommand;
import com.worldventures.dreamtrips.wallet.service.picker.WalletPickerFacebookService;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.WalletPickerFacebookPresenterImpl;

import java.util.List;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class WalletPickerFacebookAlbumsPresenterImpl extends WalletPickerFacebookPresenterImpl<WalletPickerFacebookAlbumView> implements WalletPickerFacebookAlbumsPresenter{

   private final FacebookHelper facebookHelper;
   private final WalletPickerFacebookService walletPickerFacebookService;

   public WalletPickerFacebookAlbumsPresenterImpl(FacebookHelper facebookHelper, WalletPickerFacebookService walletPickerFacebookService, FacebookInteractor facebookInteractor) {
      super(facebookInteractor);
      this.facebookHelper = facebookHelper;
      this.walletPickerFacebookService = walletPickerFacebookService;
   }

   @Override
   public void attachView(WalletPickerFacebookAlbumView view) {
      super.attachView(view);
      if (!facebookHelper.isLoggedIn()) {
         loginToFb();
      } else {
         loadItems();
      }
   }

   private void loginToFb() {
      walletPickerFacebookService
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
                     List<WalletFacebookAlbumModel> albumList = Queryable
                           .from(getAlbumsCommand.getResult())
                           .filter(album -> album.getCount() > 0)
                           .map(facebookAlbum -> new WalletFacebookAlbumModel(facebookAlbum.getId(),
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
