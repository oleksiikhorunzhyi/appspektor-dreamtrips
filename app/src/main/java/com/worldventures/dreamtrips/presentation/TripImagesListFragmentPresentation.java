package com.worldventures.dreamtrips.presentation;

import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.LoaderFactory;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.view.fragment.TripImagesListFragment;

import org.robobinding.annotation.PresentationModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@PresentationModel
public class TripImagesListFragmentPresentation extends BasePresentation<TripImagesListFragmentPresentation.View> {

    @Inject
    DreamTripsApi dreamTripsApi;

    @Inject
    LoaderFactory loaderFactory;

    private TripImagesListFragment.Type type;
    private List<Photo> photos;
    private CollectionController<Photo> photosController;

    public TripImagesListFragmentPresentation(View view, TripImagesListFragment.Type type) {
        super(view);
        this.type = type;
    }

    @Override
    public void init() {
        super.init();

        this.photosController = loaderFactory.create(this.type.ordinal(), (context, params) -> {
            this.photos = this.loadPhotos();
            return this.photos;
        });
    }

    public CollectionController<Photo> getPhotosController() {
        return photosController;
    }

    public List<Photo> loadPhotos() {
        switch (type) {
            case MY_IMAGES:
                final User user = appSessionHolder.get().get().getUser();
                return dreamTripsApi.getMyPhotos(user.getId());
            case MEMBER_IMAGES:
                return dreamTripsApi.getUserPhotos();
            case YOU_SHOULD_BE_HERE:
                return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    public void onItemClick(int position) {
        this.activityRouter.openFullScreenPhoto(this.photos, position);
    }

    public static interface View extends BasePresentation.View {

    }
}
