package com.worldventures.dreamtrips.modules.facebook.presenter;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import static com.facebook.HttpMethod.GET;


public class FacebookPhotoPresenter extends Presenter<FacebookPhotoPresenter.View> {

    Request requestForPagedResults;

    String albumId;

    Request.Callback callback = response -> {
        requestForPagedResults = response.getRequestForPagedResults(Response.PagingDirection.NEXT);
        view.handleResponse(response);
    };

    public FacebookPhotoPresenter(String albumId) {
        this.albumId = albumId;
    }

    public void onPhotoChosen(ChosenImage image) {
        view.preFinishProcessing(image);
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

    public interface View extends Presenter.View {
        void preFinishProcessing(ChosenImage image);
        void handleResponse(Response response);
    }
}
