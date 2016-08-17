package com.worldventures.dreamtrips.modules.facebook.presenter;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import static com.facebook.HttpMethod.GET;

public class FacebookAlbumPresenter extends Presenter<FacebookAlbumPresenter.View> {

   Request requestForPagedResults;

   private Request.Callback callback = response -> {
      requestForPagedResults = response.getRequestForPagedResults(Response.PagingDirection.NEXT);
      if (view != null) view.handleResponse(response);
   };

   public void requestAlbums(boolean fromScroll) {
      if (!fromScroll) {
         new Request(Session.getActiveSession(), "/me/albums", null, GET, callback).executeAsync();
      } else {
         if (requestForPagedResults != null) {
            requestForPagedResults.setCallback(callback);
            requestForPagedResults.executeAsync();
         }
      }
   }

   public interface View extends Presenter.View {
      void handleResponse(Response response);
   }

}
