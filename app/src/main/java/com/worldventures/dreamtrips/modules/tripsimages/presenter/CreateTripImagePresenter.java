package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.modules.feed.presenter.CreateEntityPresenter;

public class CreateTripImagePresenter extends CreateEntityPresenter<CreateEntityPresenter.View> {

    @Override
    public int getMediaRequestId() {
        return CreateTripImagePresenter.class.getSimpleName().hashCode();
    }
}
