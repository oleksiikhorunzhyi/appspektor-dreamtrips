package com.worldventures.dreamtrips.presentation;

import com.worldventures.dreamtrips.core.model.Photo;

import org.robobinding.annotation.PresentationModel;

@PresentationModel
public class FullScreenPhotoFragmentPM extends BasePresentation<FullScreenPhotoFragmentPM.View> {

    Photo photo;

    public FullScreenPhotoFragmentPM(View view) {
        super(view);
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void onLikeAction() {
        if (!photo.isLiked()) {
            dataManager.likePhoto(photo.getId(), (jsonObject, e) -> {
                if (e == null) {
                    photo.setLiked(true);
                    view.setLiked(true);
                }
            });
        } else {
            dataManager.unlikePhoto(photo.getId(), (jsonObject, e) -> {
                if (e == null) {
                    photo.setLiked(false);
                    view.setLiked(false);
                }
            });
        }
    }

    public void onCreate() {
        view.setTitle(photo.getTitle());
        view.setLiked(photo.isLiked());
    }

    public void flagAction(String title) {
        dataManager.flagPhoto(photo.getId(), title, (jsonObject, e) -> {
            if (e == null) {
                view.informUser("Photo has been flagged");
            } else {
                handleError(e);
            }
        });
    }


    public static interface View extends BasePresentation.View {
        void setTitle(String title);

        void setLiked(boolean isLiked);
    }
}
