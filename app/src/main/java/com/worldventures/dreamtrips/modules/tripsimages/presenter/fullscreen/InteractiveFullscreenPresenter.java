package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoDeletedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedEntityQuery;
import com.worldventures.dreamtrips.modules.feed.api.LikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.api.UnlikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.bundle.CommentsBundle;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.friends.bundle.UsersLikedEntityBundle;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.FlagPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetFlagContentQuery;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.List;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type.INSPIRE_ME;
import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type.MEMBER_IMAGES;
import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type.YOU_SHOULD_BE_HERE;

public class InteractiveFullscreenPresenter extends FullScreenPresenter<Photo> {

    private List<Flag> flags;

    private FeedEntity feedEntity;

    @Override
    public void onDeleteAction() {
        doRequest(new DeletePhotoCommand(photo.getFsId()), (jsonObject) -> {
            view.informUser(context.getString(R.string.photo_deleted));
            eventBus.postSticky(new PhotoDeletedEvent(photo.getFsId()));
        });
    }

    @Override
    public void sendFlagAction(String title, String description) {
        String desc = "";
        if (description != null) {
            desc = description;
        }
        doRequest(new FlagPhotoCommand(photo.getFsId(), title + ". " + desc), obj -> {
            view.informUser(context.getString(R.string.photo_flagged));
            TrackingHelper.flag(type, String.valueOf(photo.getFsId()), getAccountUserId());
        });
    }

    @Override
    public void onLikeAction() {
        DreamTripsRequest dreamTripsRequest = !photo.isLiked() ?
                new LikeEntityCommand(photo.getUid()) :
                new UnlikeEntityCommand(photo.getUid());
        doRequest(dreamTripsRequest, obj -> onLikeSuccess(), exc -> onLikeFailure());
    }

    @Override
    public void onCommentsAction() {
        if (feedEntity == null) {
            doRequest(new GetFeedEntityQuery(photo.getUid()), feedEntityHolder -> {
                feedEntity = feedEntityHolder.getItem();
                navigateToComments();
            });
        } else {
            navigateToComments();
        }
    }

    private void navigateToComments() {
        NavigationBuilder.create()
                .with(activityRouter)
                    .data(new CommentsBundle(feedEntity, false))
                .move(Route.COMMENTS);
    }

    @Override
    public void onLikesAction() {
        NavigationBuilder.create()
                .with(activityRouter)
                .data(new UsersLikedEntityBundle(photo.getUid()))
                .move(Route.USERS_LIKED_CONTENT);
    }

    private void onLikeFailure() {
        view.informUser(context.getString(R.string.can_not_like_photo));
    }

    private void onLikeSuccess() {
        boolean isLiked = !photo.isLiked();
        photo.setLiked(isLiked);
        int likesCount = photo.getLikesCount();
        int actualLikeCount = isLiked ? likesCount + 1 : likesCount - 1;
        photo.setLikesCount(actualLikeCount);
        view.setLiked(isLiked);
        view.setLikeCount(actualLikeCount);
        eventBus.post(new EntityLikedEvent(photo.getFsId(), isLiked));
        TrackingHelper.like(type, String.valueOf(photo.getFsId()), getAccountUserId());
    }

    @Override
    protected boolean isLiked() {
        return photo.isLiked();
    }

    @Override
    public void onEdit() {
        view.openEdit(new EditPhotoBundle(photo));
    }

    @Override
    protected boolean isFlagVisible() {
        return photo.getUser() != null && getAccount().getId() != photo.getUser().getId();
    }

    @Override
    protected boolean isDeleteVisible() {
        return false;
    }

    @Override
    protected boolean isEditVisible() {
        return photo.getUser() != null && getAccount().getId() == photo.getUser().getId();
    }

    @Override
    protected boolean isCommentVisible() {
        return true;
    }

    @Override
    protected boolean isLikeVisible() {
        return type != YOU_SHOULD_BE_HERE && type != INSPIRE_ME;
    }

    @Override
    public void onFlagAction() {
        if (flags == null) loadFlags();
        else view.setFlags(flags);
    }

    private void loadFlags() {
        view.showProgress();
        doRequest(new GetFlagContentQuery(), this::flagsLoaded, spiceException -> view.hideProgress());
    }

    private void flagsLoaded(List<Flag> flags) {
        if (view != null) {
            view.hideProgress();
            this.flags = flags;
            view.setFlags(flags);
        }
    }

    public void onEvent(FeedEntityChangedEvent event) {
        if (event.getFeedEntity() instanceof Photo) {
            Photo temp = (Photo) event.getFeedEntity();
            if (photo.equals(temp)) {
                this.photo = temp;
                setupActualViewState();
            }

        }
    }
}
