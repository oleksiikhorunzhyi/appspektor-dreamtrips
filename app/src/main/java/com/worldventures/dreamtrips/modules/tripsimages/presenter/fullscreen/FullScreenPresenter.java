package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import android.text.Spanned;

import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareFragment;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;
import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type.INSPIRE_ME;
import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type.YOU_SHOULD_BE_HERE;
import javax.inject.Inject;
import javax.inject.Named;

public abstract class FullScreenPresenter<T extends IFullScreenObject, PRESENTER_VIEW extends FullScreenPresenter.View> extends Presenter<PRESENTER_VIEW> {

    protected TripImagesType type;
    protected T photo;

    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;

    public FullScreenPresenter(T photo, TripImagesType type) {
        this.photo = photo;
        this.type = type;
    }

    @Override
    public void takeView(PRESENTER_VIEW view) {
        super.takeView(view);
        setupActualViewState();
        TrackingHelper.view(type, String.valueOf(photo.getFSId()), getAccountUserId());
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
        view.openUser(new UserBundle(user));
    }

    public final void setupActualViewState() {
        view.setContent(photo);
    }

    public void sendFlagAction(int flagReasonId, String reason) {
    }

    public void onDeleteAction() {
    }

    public void onShare(@ShareFragment.ShareType String type) {
        activityRouter.openShare(photo.getFSImage().getUrl(), null, photo.getFSShareText(), type);
        view.openShare(photo.getFSImage().getUrl(), photo.getFsShareText(), type);
        if (photo instanceof Inspiration) {
            TrackingHelper.insprShare(photo.getFSId(), type);
        }
    }

    public interface View extends Presenter.View {

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

        void openUser(UserBundle bundle);

        void openShare(String imageUrl, String text, @ShareFragment.ShareType String type);
        <T extends IFullScreenObject> void setContent(T photo);
    }
}
