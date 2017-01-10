package com.worldventures.dreamtrips.modules.facebook.presenter;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.gson.reflect.TypeToken;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.common.presenter.BasePickerPresenter;
import com.worldventures.dreamtrips.modules.facebook.FacebookHelper;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookPhoto;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.facebook.HttpMethod.GET;

public class FacebookPhotoPresenter extends BasePickerPresenter<FacebookPhotoPresenter.View> {

   private GraphRequest requestForPagedResults;

   private String albumId;

   private int previousTotal;
   private boolean loading;

   @Inject FacebookHelper facebookHelper;

   GraphRequest.Callback callback = response -> {
      requestForPagedResults = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
      if (view != null) handleResponse(response);
   };

   public FacebookPhotoPresenter(String albumId) {
      super();
      this.albumId = albumId;
   }

   private void handleResponse(GraphResponse response) {
      List<BasePhotoPickerModel> photosList = new ArrayList<>(facebookHelper
            .processList(response, new TypeToken<List<FacebookPhoto>>(){}));
      photos.addAll(photosList);
      view.addItems(new ArrayList<>(photosList));
   }

   public void scrolled(int totalItemCount, int lastVisible) {
      if (totalItemCount > previousTotal) {
         loading = false;
         previousTotal = totalItemCount;
      }
      if (!loading && lastVisible == totalItemCount - 1) {
         requestPhotos(true);
         loading = true;
      }
   }

   public void requestPhotos(boolean fromScroll) {
      if (!fromScroll) {
         String route = "/{album-id}/photos?fields=id,images".replace("{album-id}", albumId);
         new GraphRequest(AccessToken.getCurrentAccessToken(), route, null, GET, callback).executeAsync();
      } else {
         if (requestForPagedResults != null) {
            requestForPagedResults.setCallback(callback);
            requestForPagedResults.executeAsync();
         }
      }
   }

   public interface View extends BasePickerPresenter.View {}
}
