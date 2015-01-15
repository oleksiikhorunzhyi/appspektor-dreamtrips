package com.worldventures.dreamtrips.view.presentation;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.model.Photo;

import org.robobinding.annotation.PresentationModel;

@PresentationModel
public class FullScreenPhotoFragmentPM extends BasePresentation {


    private View view;

    public FullScreenPhotoFragmentPM(View view, Injector injector) {
        super(view, injector);
        this.view = view;
    }

    Photo photo;

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public void onLikeAction() {
        if (!photo.isLiked()) {
            dataManager.likePhoto(sessionManager, photo.getId(), (jsonObject, e) -> {
                if (e == null) {
                    photo.setLiked(true);
                    view.setLiked(true);
                }
            });
        } else {
            dataManager.unlikePhoto(sessionManager, photo.getId(), (jsonObject, e) -> {
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
        dataManager.flagPhoto(sessionManager, photo.getId(), title, (jsonObject, e) -> {
            if (e == null) {
                view.informUser("Photo has been flagged");
            } else {
                handleError(e);
            }
        });
    }


    public static interface View extends IInformView {
        void setTitle(String title);

        void setLiked(boolean isLiked);
    }
}
