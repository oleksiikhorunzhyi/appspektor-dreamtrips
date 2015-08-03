package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.List;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;
import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type.INSPIRE_ME;
import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type.YOU_SHOULD_BE_HERE;

public abstract class FullScreenPresenter<T extends IFullScreenObject> extends Presenter<FullScreenPresenter.View> {

    protected Type type;
    protected T photo;

    public static FullScreenPresenter create(IFullScreenObject photo) {
        if (photo instanceof Photo) {
            return new InteractiveFullscreenPresenter();
        } else if (photo instanceof BucketPhoto) {
            return new BucketFullscreenPresenter();
        }
        return new SimpleFullscreenPresenter();
    }

    public void setPhoto(T photo) {
        this.photo = photo;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        setupActualViewState();
        view.setSocial(featureManager.available(Feature.SOCIAL));
        TrackingHelper.view(type, String.valueOf(photo.getFsId()), getAccountUserId());
    }

    public void onLikeAction() {
    }

    public void onFlagAction() {
    }

    public void showFlagAction(int order) {
    }

    public void onUserClicked() {
        User user = photo.getUser();
        activityRouter.openUserProfile(user);
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
        view.setUserPhoto(photo.getFsUserPhoto());

        if (photo instanceof Inspiration) {
            TrackingHelper.insprDetails(getAccountUserId(), photo.getFsId());
        }

    }

    protected abstract boolean isFlagVisible();

    protected abstract boolean isDeleteVisible();

    protected abstract boolean isLikeVisible();

    protected boolean isLiked() {
        return false;
    }

    private boolean isLikeCountVisible() {
        return type != YOU_SHOULD_BE_HERE && type != INSPIRE_ME;
    }

    public void sendFlagAction(String title, String desc) {
    }

    public void onDeleteAction() {
    }

    public void onFbShare() {
        activityRouter.openShareFacebook(photo.getFSImage().getUrl(), null, photo.getFsShareText());
        if (photo instanceof Inspiration) {
            TrackingHelper.insprShare(photo.getFsId(), "facebook");
        }
    }

    public void onTwitterShare() {
        activityRouter.openShareTwitter(photo.getFSImage().getUrl(), null, photo.getFsShareText());
        if (photo instanceof Inspiration) {
            TrackingHelper.insprShare(photo.getFsId(), "twitter");
        }
    }

    public void onCheckboxPressed(boolean status) {
    }

    public void openComments() {
        activityRouter.openCommentsScreen();
    }

    public interface View extends Presenter.View {
        void setTitle(String title);

        void showCheckbox(boolean status);

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

        void showFlagConfirmDialog(String reason, String desc);

        void showFlagDescription(String reason);

        void setLikeCountVisibility(boolean likeCountVisible);

        void setUserPhoto(String fsPhoto);

        void setFlags(List<Flag> flags);

        void showProgress();

        void hideProgress();

        void setSocial(Boolean isEnabled);

        void showCoverProgress();

        void hideCoverProgress();
    }
}
