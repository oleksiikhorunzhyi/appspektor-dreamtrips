package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapperFactory;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoDeletedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.UidItemDelegate;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedEntityQuery;
import com.worldventures.dreamtrips.modules.feed.bundle.CommentsBundle;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.friends.bundle.UsersLikedEntityBundle;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type.INSPIRE_ME;
import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type.YOU_SHOULD_BE_HERE;

public class InteractiveFullscreenPresenter extends FullScreenPresenter<Photo> {

    @Inject
    FeedEntityManager entityManager;

    UidItemDelegate uidItemDelegate;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        uidItemDelegate = new UidItemDelegate(this);
        loadEntity(feedEntityHolder -> {
            FeedEntity feedEntity = feedEntityHolder.getItem();
            photo.syncLikeState(feedEntity);
            photo.setCommentsCount(feedEntity.getCommentsCount());
            photo.setComments(feedEntity.getComments());
            setupActualViewState();
        });
    }

    @Override
    public void onInjected() {
        super.onInjected();
        entityManager.setRequestingPresenter(this);
    }

    private void loadEntity(DreamSpiceManager.SuccessListener<FeedEntityHolder> successListener) {
        doRequest(new GetFeedEntityQuery(photo.getUid()), successListener);
    }

    @Override
    public void onDeleteAction() {
        doRequest(new DeletePhotoCommand(photo.getFsId()), (jsonObject) -> {
            view.informUser(context.getString(R.string.photo_deleted));
            eventBus.postSticky(new PhotoDeletedEvent(photo.getFsId()));
        });
    }

    @Override
    public void sendFlagAction(int flagReasonId, String reason) {
        uidItemDelegate.flagItem(new FlagData(photo.getUid(),
                flagReasonId, reason));
    }

    @Override
    public void onLikeAction() {
        if (!photo.isLiked()) {
            entityManager.like(photo);
        } else {
            entityManager.unlike(photo);
        }
    }

    public void onEvent(EntityLikedEvent event) {
        photo.syncLikeState(event.getFeedEntity());
        view.setLiked(photo.isLiked());
        view.setLikeCount(photo.getLikesCount());
        TrackingHelper.like(type, String.valueOf(photo.getFsId()), getAccountUserId());
    }

    @Override
    protected boolean isLiked() {
        return photo.isLiked();
    }

    @Override
    public void onCommentsAction() {
        new NavigationWrapperFactory()
                .componentOrDialogNavigationWrapper(activityRouter, fragmentCompass, view)
                .navigate(Route.COMMENTS, new CommentsBundle(photo, false));

    }

    @Override
    public void onLikesAction() {
        new NavigationWrapperFactory()
                .componentOrDialogNavigationWrapper(activityRouter, fragmentCompass, view)
                .navigate(Route.USERS_LIKED_CONTENT, new UsersLikedEntityBundle(photo.getUid()));
    }

    @Override
    public void onEdit() {
        if (view != null) view.openEdit(new EditPhotoBundle(photo));
    }

    @Override
    protected boolean isFlagVisible() {
        return photo.getOwner() != null && getAccount().getId() != photo.getOwner().getId();
    }

    @Override
    protected boolean isDeleteVisible() {
        return false;
    }

    @Override
    protected boolean isEditVisible() {
        return photo.getOwner() != null && getAccount().getId() == photo.getOwner().getId();
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
    public void onFlagAction(Flaggable flaggable) {
        view.showProgress();
        uidItemDelegate.loadFlags(flaggable);
    }

    public void onEvent(FeedEntityChangedEvent event) {
        updatePhoto(event.getFeedEntity());
    }

    public void onEvent(FeedEntityCommentedEvent event) {
        updatePhoto(event.getFeedEntity());
    }

    private void updatePhoto(FeedEntity feedEntity) {
        if (feedEntity instanceof Photo) {
            Photo temp = (Photo) feedEntity;
            if (photo.equals(temp)) {
                this.photo = temp;
                setupActualViewState();
            }
        }
    }
}
