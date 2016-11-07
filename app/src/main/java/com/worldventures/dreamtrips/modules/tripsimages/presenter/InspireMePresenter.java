package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetInspireMePhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;

public class InspireMePresenter extends TripImagesListPresenter<TripImagesListPresenter.View> {
   protected double randomSeed;

   public InspireMePresenter(int userId) {
      super(TripImagesType.INSPIRE_ME, userId);
   }

   @Override
   public SpiceRequest<ArrayList<IFullScreenObject>> getReloadRequest() {
      randomSeed = Math.random();
      return new GetInspireMePhotosQuery(PER_PAGE, 1, randomSeed);
   }

   @Override
   public SpiceRequest<ArrayList<IFullScreenObject>> getNextPageRequest(int currentPage) {
      return new GetInspireMePhotosQuery(PER_PAGE, currentPage, randomSeed);
   }
}
