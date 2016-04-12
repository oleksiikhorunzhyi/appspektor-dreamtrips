package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import android.support.v4.app.FragmentManager;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapperFactory;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoDeletedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.UidItemDelegate;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedEntityQuery;
import com.worldventures.dreamtrips.modules.feed.bundle.CommentsBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.EditEntityBundle;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityDeletedEvent;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SocialImageFullscreenPresenter extends FullScreenPresenter<Photo, SocialImageFullscreenPresenter.View> {

    @Inject
    FeedEntityManager entityManager;

    UidItemDelegate uidItemDelegate;

    public SocialImageFullscreenPresenter(Photo photo, TripImagesType type) {
        super(photo, type);
    }

    @Inject
    Router router;
    @Inject
    FragmentManager fm;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        uidItemDelegate = new UidItemDelegate(this);
        loadEntity();
    }

    @Override
    public void onInjected() {
        super.onInjected();
        entityManager.setRequestingPresenter(this);
    }

    public void loadEntity() {
        doRequest(new GetFeedEntityQuery(photo.getUid()), feedEntityHolder -> {
            FeedEntity feedEntity = feedEntityHolder.getItem();
            if (photo.getUser() != null) {
                photo.syncLikeState(feedEntity);
                photo.setCommentsCount(feedEntity.getCommentsCount());
                photo.setComments(feedEntity.getComments());
                photo.setPhotoTags(((Photo) feedEntity).getPhotoTags());
            } else {
                photo = (Photo) feedEntity;
                view.showContentWrapper();
            }
            setupActualViewState();
        });
    }

    @Override
    public void onDeleteAction() {
        doRequest(new DeletePhotoCommand(photo.getFSId()), (jsonObject) -> {
            view.informUser(context.getString(R.string.photo_deleted));
            eventBus.postSticky(new PhotoDeletedEvent(photo.getFSId()));
            eventBus.postSticky(new FeedEntityDeletedEvent(photo));
        });
    }


    public void deleteTag(PhotoTag tag) {
        List<Integer> userIds = new ArrayList<>();
        userIds.add(tag.getTargetUserId());
        doRequest(new DeletePhotoTagsCommand(photo.getFSId(), userIds), aVoid -> {
            photo.getPhotoTags().remove(tag);
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
        view.setContent(photo);
        TrackingHelper.like(type, String.valueOf(photo.getFSId()), getAccountUserId());
    }

    @Override
    public void onCommentsAction() {
        new NavigationWrapperFactory()
                .componentOrDialogNavigationWrapper(router, fm, view)
                .navigate(Route.COMMENTS, new CommentsBundle(photo, false, true));

    }

    @Override
    public void onLikesAction() {
        onCommentsAction();
    }

    @Override
    public void onEdit() {
        if (view != null) view.openEdit(new EditEntityBundle(photo, FeedEntityHolder.Type.PHOTO));
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
                view.redrawTags();
            }
        }
    }


    public interface View extends FullScreenPresenter.View {

        void showProgress();

        void hideProgress();

        void showContentWrapper();

        void openEdit(EditEntityBundle bundle);

        void redrawTags();
    }
}
