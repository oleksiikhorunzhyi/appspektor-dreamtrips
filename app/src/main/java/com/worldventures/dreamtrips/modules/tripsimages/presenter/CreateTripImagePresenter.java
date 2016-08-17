package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.modules.feed.presenter.CreateEntityPresenter;

public class CreateTripImagePresenter extends CreateEntityPresenter<CreateEntityPresenter.View> {

   public static final int REQUEST_ID = CreateTripImagePresenter.class.getSimpleName().hashCode();

   @Override
   public int getMediaRequestId() {
      return REQUEST_ID;
   }
}
