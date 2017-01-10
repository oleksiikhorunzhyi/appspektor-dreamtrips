package com.worldventures.dreamtrips.modules.facebook.presenter;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.reflect.TypeToken;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.facebook.FacebookHelper;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookAlbum;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import static com.facebook.HttpMethod.GET;

public class FacebookAlbumPresenter extends Presenter<FacebookAlbumPresenter.View> {

   private GraphRequest requestForPagedResults;
   private boolean facebookLoginCanceledOrFailed;

   @Inject FacebookHelper facebookHelper;

   private GraphRequest.Callback callback = response -> {
      requestForPagedResults = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
      if (view != null) view.showAlbums(processResponse(response));
   };

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
   }

   @Override
   public void onResume() {
      super.onResume();
      if (!facebookHelper.isLoggedIn() && facebookLoginCanceledOrFailed) {
         view.informUser(R.string.facebook_login_error);
         view.back();
      }
      if (view.getItemsCount() == 0) {
         requestAlbums(false);
      }
   }

   public void requestAlbums(boolean fromScroll) {
      if (!fromScroll) {
         new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/albums?fields=id,name,count,cover_photo",
               null, GET, callback).executeAsync();
      } else {
         if (requestForPagedResults != null) {
            requestForPagedResults.setCallback(callback);
            requestForPagedResults.executeAsync();
         }
      }
   }

   private List<FacebookAlbum> processResponse(GraphResponse response) {
      List<FacebookAlbum> albumList = facebookHelper.processList(response, new TypeToken<List<FacebookAlbum>>(){});
      albumList = Queryable.from(albumList).filter(album -> album.getCount() > 0).toList();
      return albumList;
   }

   public interface View extends Presenter.View {
      void setCallbackManager(CallbackManager callbackManager);

      void loginToFacebook(Collection<String> permissions);

      int getItemsCount();

      void showAlbums(List<FacebookAlbum> albums);

      void back();
   }
}
