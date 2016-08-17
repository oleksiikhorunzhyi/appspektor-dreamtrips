package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetYSBHPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;

public class YSBHPresenter extends TripImagesListPresenter<TripImagesListPresenter.View> {
   public YSBHPresenter(int userId) {
      super(TripImagesType.YOU_SHOULD_BE_HERE, userId);
   }

   @Override
   public SpiceRequest<ArrayList<IFullScreenObject>> getReloadRequest() {
      return new GetYSBHPhotosQuery(PER_PAGE, 1);
   }

   @Override
   public SpiceRequest<ArrayList<IFullScreenObject>> getNextPageRequest(int currentCount) {
      return new GetYSBHPhotosQuery(PER_PAGE, currentCount / PER_PAGE + 1);
   }
}