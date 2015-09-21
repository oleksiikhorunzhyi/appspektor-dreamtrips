package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import android.text.Spanned;

import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;
import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type.INSPIRE_ME;
import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type.YOU_SHOULD_BE_HERE;

public abstract class FullScreenPresenter<T extends IFullScreenObject> extends Presenter<FullScreenPresenter.View> {

    protected Type type;
    protected T photo;

    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;

    public static FullScreenPresenter create(IFullScreenObject photo, boolean foreign) {
        if (photo instanceof Photo) {
            return new InteractiveFullscreenPresenter();
        } else if (photo instanceof BucketPhoto) {
            return new BucketFullscreenPresenter(foreign);
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

    public void onEdit() {
    }

    public void onLikeAction() {
    }

    public void onFlagAction() {
    }

    public void showFlagAction(int order) {
    }

    public void onUserClicked() {
        User user = photo.getUser();
        NavigationBuilder.create().with(activityRouter)
                .data(new UserBundle(user))
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .move(routeCreator.createRoute(user.getId()));

    }

    public final void setupActualViewState() {
        if (photo instanceof Photo && photo.getUser() != null) {
            view.setTitleSpanned(photo.getUser().getUsernameWithCompany(context));
        } else {
            view.setTitle(photo.getFSTitle());
        }

        view.setLiked(isLiked());
        view.setCommentVisibility(false);

        view.setLikeVisibility(isLikeVisible());
        view.setLikeCountVisibility(isLikeCountVisible());
        view.setDeleteVisibility(isDeleteVisible());
        view.setMoreVisibility(isMoreVisible());
        view.setShareVisibility(isShareVisible());
        view.setFlagVisibility(isFlagVisible());
        view.loadImage(photo.getFSImage());
        view.setDescription(photo.getFsDescription());
        view.setCommentCount(photo.getFsCommentCount());
        view.setLikeCount(photo.getFsLikeCount());
        view.setLocation(photo.getFsLocation());
        view.setDate(photo.getFsDate());
        view.setUserPhoto(photo.getFsUserPhoto());
        view.setContentDividerVisibility(isLikeVisible() || isLikeCountVisible() ||
                isDeleteVisible() || isFlagVisible());

        if (photo instanceof Inspiration) {
            TrackingHelper.insprDetails(getAccountUserId(), photo.getFsId());
        }

    }

    protected abstract boolean isFlagVisible();

    protected abstract boolean isDeleteVisible();

    protected abstract boolean isLikeVisible();

    protected abstract boolean isMoreVisible();

    protected boolean isLiked() {
        return false;
    }

    protected boolean isShareVisible() {
        return true;
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

    public interface View extends Presenter.View {
        void setTitle(String title);

        void setTitleSpanned(Spanned titleSpanned);

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

        void setContentDividerVisibility(boolean b);

        void setCommentVisibility(boolean commentVisible);

        void setMoreVisibility(boolean visible);

        void openEdit(EditPhotoBundle editPhotoBundle);

        void setShareVisibility(boolean shareVisible);
    }
}
