package com.worldventures.dreamtrips.presentation.fullscreen;

import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.core.model.Image;
import com.worldventures.dreamtrips.core.model.Inspiration;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.core.model.config.Flag;
import com.worldventures.dreamtrips.presentation.BasePresentation;
import com.worldventures.dreamtrips.view.activity.FullScreenPhotoActivity;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type;

public abstract class BaseFSViewPM<T extends IFullScreenAvailableObject> extends BasePresentation<BaseFSViewPM.View> {

    @Inject
    @Global
    EventBus eventBus;

    T photo;
    protected Type type;
    protected User user;

    public BaseFSViewPM(View view) {
        super(view);

    }

    public void setupPhoto(T photo) {
        this.photo = photo;
    }

    public void setupType(Type type) {
        this.type = type;
    }

    public T providePhoto() {
        return photo;
    }

    public void onCreate() {
        user = appSessionHolder.get().get().getUser();
    }

    public void onLikeAction() {

    }

    public final void setupActualViewState() {
        view.setTitle(photo.getFSTitle());
        view.setLiked(isLiked());
        view.setLikeVisibility(isLikeVisible());
        view.setDeleteVisibility(isDeleteVisible());
        view.setFlagVisibility(isFlagVisible());
        view.loadImage(photo.getFSImage());
        view.setInspireDescription(photo.getFsDescription());
    }

    protected abstract boolean isLiked();

    protected abstract boolean isFlagVisible();

    protected abstract boolean isDeleteVisible();

    protected abstract boolean isLikeVisible();

    public void showFlagAction(int itemId) {
        Flag flagContent = getFlagContent().get(itemId);
        if (flagContent.isNeedDescription()) {
            view.showFlagDescription(flagContent.getCode());
        } else {
            view.showFlagConfirmDialog(flagContent.getCode(), null);
        }
    }

    public void sendFlagAction(String title, String desc) {
    }

    public void onDeleteAction() {
    }

    public void onFbShare(FullScreenPhotoActivity activity) {
        activityRouter.openShareFacebook(photo.getFSImage().getMedium().getUrl(), photo.getFsShareText());
    }

    public void onTwitterShare(FullScreenPhotoActivity activity) {

        activityRouter.openShareTwitter(photo.getFSImage().getMedium().getUrl(), photo.getFsShareText());
    }

    public static BaseFSViewPM create(View view, IFullScreenAvailableObject photo) {
        if (photo instanceof Photo) {
            return new FSPhotoPM(view);
        } else if (photo instanceof Inspiration) {
            return new FSInspireMePM(view);
        }
        return new ImageUploadTaskPM(view);
    }

    public List<Flag> getFlagContent() {
        return appSessionHolder.get().get().getGlobalConfig().getFlagContent().getDefault();
    }

    public static interface View extends BasePresentation.View {
        void setTitle(String title);

        void setInspireDescription(String desc);

        void setLiked(boolean isLiked);

        void loadImage(Image image);

        void setFlagVisibility(boolean isVisible);

        void setDeleteVisibility(boolean isVisible);

        void setLikeVisibility(boolean isVisible);

        public void showFlagConfirmDialog(String reason, String desc);

        public void showFlagDescription(String reason);
    }
}
