package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;


public class DetailedImagePresenter extends Presenter<Presenter.View> {

    protected IFullScreenObject photo;

    public IFullScreenObject getPhoto() {
        return photo;
    }

    public void setPhoto(IFullScreenObject photo) {
        this.photo = photo;
    }

}

