package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.utils.AdobeTrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;
import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type.INSPIRE_ME;
import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type.YOU_SHOULD_BE_HERE;

public abstract class FSViewPM<T extends IFullScreenAvailableObject> extends Presenter<FSViewPM.View> {

    protected Type type;
    protected User user;
    @Inject
    @Global
    EventBus eventBus;
    T photo;

    public FSViewPM(View view) {
        super(view);

    }

    public static FSViewPM create(View view, IFullScreenAvailableObject photo) {
        if (photo instanceof Photo) {
            return new FSPhotoPM(view);
        } else if (photo instanceof Inspiration) {
            return new FSInspireMePM(view);
        }
        return new ImageUploadTaskPM(view);
    }

    public void setupPhoto(T photo) {
        this.photo = photo;
    }

    public void setupType(Type type) {
        this.type = type;
        AdobeTrackingHelper.view(type, String.valueOf(photo.getId()), getUserId());

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
        view.setLikeCountVisibility(isLikeCountVisible());
        view.setDeleteVisibility(isDeleteVisible());
        view.setFlagVisibility(isFlagVisible());
        view.loadImage(photo.getFSImage());
        view.setDescription(photo.getFsDescription());
        view.setCommentCount(photo.getFsCommentCount());
        view.setLikeCount(photo.getFsLikeCount());
        view.setLocation(photo.getFsLocation());
        view.setDate(photo.getFsDate());
    }



    protected abstract boolean isLiked();

    protected abstract boolean isFlagVisible();

    protected abstract boolean isDeleteVisible();

    protected abstract boolean isLikeVisible();

    private boolean isLikeCountVisible() {
        return type != YOU_SHOULD_BE_HERE && type != INSPIRE_ME;
    }

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

    public void onFbShare() {
        activityRouter.openShareFacebook(photo.getFSImage().getMedium().getUrl(), null, photo.getFsShareText());
    }

    public void onTwitterShare() {
        activityRouter.openShareTwitter(photo.getFSImage().getMedium().getUrl(), null, photo.getFsShareText());
    }

    public List<Flag> getFlagContent() {
        return appSessionHolder.get().get().getGlobalConfig().getFlagContent().getDefault();
    }

    public static interface View extends Presenter.View {
        void setTitle(String title);

        void setDate(String date);

        void setLocation(String location);

        void setCommentCount(int count);

        void setLikeCount(int count);

        void setDescription(String desc);

        void setLiked(boolean isLiked);

        void loadImage(Image image);

        void setFlagVisibility(boolean isVisible);

        void setDeleteVisibility(boolean isVisible);

        void setLikeVisibility(boolean isVisible);

        public void showFlagConfirmDialog(String reason, String desc);

        public void showFlagDescription(String reason);

        void setLikeCountVisibility(boolean likeCountVisible);
    }
}
