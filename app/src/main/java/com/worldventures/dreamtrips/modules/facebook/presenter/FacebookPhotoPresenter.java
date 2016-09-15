package com.worldventures.dreamtrips.modules.facebook.presenter;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.common.presenter.BasePickerPresenter;
import com.worldventures.dreamtrips.modules.facebook.FacebookUtils;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookPhoto;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.HttpMethod.GET;

public class FacebookPhotoPresenter extends BasePickerPresenter<FacebookPhotoPresenter.View> {

   Request requestForPagedResults;

   String albumId;

   int previousTotal;
   boolean loading;

   Request.Callback callback = response -> {
      requestForPagedResults = response.getRequestForPagedResults(Response.PagingDirection.NEXT);
      if (view != null) handleResponse(response);
   };

   private void handleResponse(Response response) {
      List<GraphObject> graphObjects = FacebookUtils.typedListFromResponse(response, GraphObject.class);
      List<BasePhotoPickerModel> tempList = new ArrayList<>();
      for (GraphObject graphObject : graphObjects) {
         FacebookPhoto photo = FacebookPhoto.create(graphObject);
         tempList.add(photo);
      }
      //
      photos.addAll(tempList);
      view.addItems(tempList);
   }

   public FacebookPhotoPresenter(String albumId) {
      super();
      this.albumId = albumId;
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
         String route = "/{album-id}/photos".replace("{album-id}", albumId);
         new Request(Session.getActiveSession(), route, null, GET, callback).executeAsync();
      } else {
         if (requestForPagedResults != null) {
            requestForPagedResults.setCallback(callback);
            requestForPagedResults.executeAsync();
         }
      }
   }

   public interface View extends BasePickerPresenter.View {}
}
