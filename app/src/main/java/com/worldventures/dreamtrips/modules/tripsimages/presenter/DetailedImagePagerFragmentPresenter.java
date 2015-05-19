package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;


public class DetailedImagePagerFragmentPresenter extends Presenter<Presenter.View> {

    protected TripImage photo;

    public TripImage getPhoto() {
        return photo;
    }

    public void setPhoto(TripImage photo) {
        this.photo = photo;
    }

}

