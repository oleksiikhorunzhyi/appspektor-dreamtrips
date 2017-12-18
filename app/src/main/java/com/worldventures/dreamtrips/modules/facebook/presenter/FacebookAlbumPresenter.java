package com.worldventures.dreamtrips.modules.facebook.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.modules.facebook.FacebookHelper;
import com.worldventures.core.modules.facebook.model.FacebookAlbum;
import com.worldventures.core.modules.facebook.service.FacebookInteractor;
import com.worldventures.core.modules.facebook.service.command.GetAlbumsCommand;
import com.worldventures.core.modules.picker.service.MediaPickerFacebookService;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class FacebookAlbumPresenter extends Presenter<FacebookAlbumPresenter.View> {

   @Inject FacebookHelper facebookHelper;
   @Inject FacebookInteractor facebookInteractor;
   @Inject MediaPickerFacebookService mediaPickerFacebookService;

   @Override
   public void takeView(View view) {
      super.takeView(view);
      observe();
      if (!facebookHelper.isLoggedIn()) {
         loginToFb();
      } else {
         requestAlbums(false);
      }
   }

   private void loginToFb() {
      mediaPickerFacebookService
            .checkFacebookLogin(FacebookHelper.LOGIN_PERMISSIONS)
            .compose(bindViewToMainComposer())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(notification -> {
               if (notification.isOnNext()) {
                  requestAlbums(false);
               } else if (notification.isOnCompleted()) {
                  view.back();
               } else {
                  Timber.e(notification.getThrowable(), "Cannot perform login");
               }
            });
   }

   private void observe() {
      facebookInteractor.albumsPipe().observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetAlbumsCommand>()
                  .onSuccess(getAlbumsCommand -> {
                     List<FacebookAlbum> albumList = getAlbumsCommand.getResult();
                     albumList = Queryable.from(albumList).filter(album -> album.getCount() > 0).toList();
                     view.showAlbums(albumList);
                  })
                  .onFail((getAlbumsCommand, throwable) -> {
                     view.back();
                     handleError(getAlbumsCommand, throwable);
                  }));
   }


   public void requestAlbums(boolean fromScroll) {
      facebookInteractor.albumsPipe().send(fromScroll ? GetAlbumsCommand.loadMore() : GetAlbumsCommand.refresh());
   }

   public interface View extends Presenter.View {
      void showAlbums(List<FacebookAlbum> albums);

      void back();
   }
}
