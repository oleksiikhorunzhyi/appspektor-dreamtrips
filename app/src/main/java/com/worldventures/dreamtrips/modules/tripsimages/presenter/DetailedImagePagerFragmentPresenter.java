package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.core.model.TripImage;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;


public class DetailedImagePagerFragmentPresenter extends BasePresenter<BasePresenter.View> {

    TripImage photo;


    public DetailedImagePagerFragmentPresenter(View view) {
        super(view);
    }

    public TripImage getPhoto() {
        return photo;
    }

    public void setPhoto(TripImage photo) {
        this.photo = photo;
    }

}

