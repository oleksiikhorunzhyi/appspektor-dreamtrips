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
import com.worldventures.dreamtrips.modules.common.view.activity.ShareFragment;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;

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

    public static FullScreenPresenter create(IFullScreenObject photo, Type type, boolean foreign) {
        switch (type) {
            case FIXED_LIST:
            case MEMBER_IMAGES:
            case MY_IMAGES:
                if (photo instanceof BucketPhoto) return new BucketFullscreenPresenter(foreign);
                else if (photo instanceof TripImage) return new SimpleFullscreenPresenter();
                else return new InteractiveFullscreenPresenter();
            case YOU_SHOULD_BE_HERE:
            case INSPIRE_ME:
                return new InspirationFullscreenPresenter();
            case FOREIGN_IMAGES:
            case VIDEO_360:
            default:
                return new SimpleFullscreenPresenter();
        }
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

    public void onFlagAction(Flaggable flaggable) {
    }

    public void onCommentsAction() {

    }

    public void onLikesAction() {

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
        view.setCommentVisibility(isCommentVisible());

        view.setLikeVisibility(isLikeVisible());
        view.setLikeCountVisibility(isLikeCountVisible());
        view.setDeleteVisibility(isDeleteVisible());
        view.setEditVisibility(isEditVisible());
        view.setShareVisibility(isShareVisible());
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

    protected abstract boolean isEditVisible();

    protected abstract boolean isCommentVisible();

    protected boolean isLiked() {
        return false;
    }

    protected boolean isShareVisible() {
        return true;
    }

    private boolean isLikeCountVisible() {
        return type != YOU_SHOULD_BE_HERE && type != INSPIRE_ME;
    }

    public void sendFlagAction(int flagReasonId, String reason) {
    }

    public void onDeleteAction() {
    }

    public void onShare(@ShareFragment.ShareType String type) {
        activityRouter.openShare(photo.getFSImage().getUrl(), null, photo.getFsShareText(), type);
        if (photo instanceof Inspiration) {
            TrackingHelper.insprShare(photo.getFsId(), type);
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

        void setLikeCountVisibility(boolean likeCountVisible);

        void setUserPhoto(String fsPhoto);

        void showProgress();

        void hideProgress();

        void setSocial(Boolean isEnabled);

        void showCoverProgress();

        void hideCoverProgress();

        void setCommentVisibility(boolean commentVisible);

        void setEditVisibility(boolean visible);

        void openEdit(EditPhotoBundle editPhotoBundle);

        void setShareVisibility(boolean shareVisible);
    }
}
