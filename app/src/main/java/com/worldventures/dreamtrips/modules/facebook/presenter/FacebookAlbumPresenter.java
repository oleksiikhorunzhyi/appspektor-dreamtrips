package com.worldventures.dreamtrips.modules.facebook.presenter;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.facebook.FacebookHelper;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookAlbum;
import com.worldventures.dreamtrips.modules.facebook.service.FacebookInteractor;
import com.worldventures.dreamtrips.modules.facebook.service.command.GetAlbumsCommand;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class FacebookAlbumPresenter extends Presenter<FacebookAlbumPresenter.View> {

   private boolean facebookLoginCanceledOrFailed;

   @Inject FacebookHelper facebookHelper;
   @Inject FacebookInteractor facebookInteractor;

   @Override
   public void takeView(View view) {
      super.takeView(view);
      CallbackManager callbackManager = CallbackManager.Factory.create();
      view.setCallbackManager(callbackManager);
      LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
         @Override
         public void onSuccess(final LoginResult loginResult) {
            facebookLoginCanceledOrFailed = false;
         }

         @Override
         public void onCancel() {
            facebookLoginCanceledOrFailed = true;
         }

         @Override
         public void onError(final FacebookException exception) {
            facebookLoginCanceledOrFailed = true;
         }
      });
      if (!facebookHelper.isLoggedIn()) {
         view.loginToFacebook(FacebookHelper.LOGIN_PERMISSIONS);
      }
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

   @Override
   public void onResume() {
      super.onResume();
      if (!facebookHelper.isLoggedIn() && facebookLoginCanceledOrFailed) {
         view.informUser(R.string.facebook_login_error);
         view.back();
         return;
      }
      if (view.getItemsCount() == 0) {
         requestAlbums(false);
      }
   }

   public void requestAlbums(boolean fromScroll) {
      facebookInteractor.albumsPipe().send(fromScroll ? GetAlbumsCommand.loadMore() : GetAlbumsCommand.refresh());
   }

   public interface View extends Presenter.View {
      void setCallbackManager(CallbackManager callbackManager);

      void loginToFacebook(Collection<String> permissions);

      int getItemsCount();

      void showAlbums(List<FacebookAlbum> albums);

      void back();
   }
}
