package com.worldventures.dreamtrips.modules.feed;

import com.techery.spares.module.qualifier.Global;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.GalleryPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.DtGalleryFragment;
import com.worldventures.dreamtrips.modules.feed.presenter.ActionEntityPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.BaseCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.CreateEntityPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.CreateFeedPostPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.EditCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.EditPhotoPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.EditPostPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedEntityDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedItemAdditionalInfoPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedItemDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedListAdditionalInfoPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.LocationPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.NotificationPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.DescriptionCreatorPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.SuggestedPhotoCellPresenterHelper;
import com.worldventures.dreamtrips.modules.feed.view.cell.BucketFeedEntityDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.BucketFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.CommentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedEntityDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedItemCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.HashtagSuggestionCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.LoadMoreCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoGalleryCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoPostCreationCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PickerIrregularPhotoCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PostCreationTextCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PostFeedItemCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PostFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.SubPhotoAttachmentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.SuggestedPhotosCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.SuggestionPhotoCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.TripFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.UndefinedFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.notification.NotificationCell;
import com.worldventures.dreamtrips.modules.feed.view.fragment.ActionEntityFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CommentableFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CreateEntityFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CreateFeedPostFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.DescriptionCreatorFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.EditCommentFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.EditPhotoFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.EditPostFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedDetailsFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedEntityDetailsFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedItemAdditionalInfoFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedItemDetailsFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedListAdditionalInfoFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.LocationFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.NotificationFragment;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedActionPanelViewActionHandler;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedEntityContentFragmentFactory;
import com.worldventures.dreamtrips.modules.feed.view.util.FragmentWithFeedDelegate;
import com.worldventures.dreamtrips.modules.feed.view.util.StatePaginatedRecyclerViewManager;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.CreateTripImageFragment;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module(
        injects = {
                TripFeedItemDetailsCell.class,
                FeedPresenter.class,
                SuggestedPhotoCellPresenterHelper.class,
                FeedFragment.class,
                BucketFeedItemDetailsCell.class,
                LoadMoreCell.class,
                PhotoFeedItemDetailsCell.class,
                PostFeedItemCell.class,
                UndefinedFeedItemDetailsCell.class,

                PickerIrregularPhotoCell.class,
                PhotoGalleryCell.class,

                EditCommentPresenter.class,

                CommentableFragment.class,
                ComponentPresenter.class,
                FeedItemDetailsFragment.class,
                FeedItemDetailsPresenter.class,
                FeedDetailsFragment.class,
                FeedDetailsPresenter.class,
                FeedEntityDetailsFragment.class,
                FeedEntityDetailsPresenter.class,
                CommentCell.class,
                BaseCommentPresenter.class,

                NotificationFragment.class,
                NotificationPresenter.class,
                NotificationCell.class,
                NotificationFragment.NotificationAdapter.class,

                FeedItemDetailsCell.class,
                FeedItemCell.class,
                BaseFeedCell.class,
                FeedEntityDetailsCell.class,
                BucketFeedEntityDetailsCell.class,
                TripFeedItemDetailsCell.class,

                FeedListAdditionalInfoFragment.class,
                FeedListAdditionalInfoPresenter.class,
                FeedItemAdditionalInfoFragment.class,
                FeedItemAdditionalInfoPresenter.class,

                EditCommentFragment.class,
                EditCommentPresenter.class,

                DtGalleryFragment.class,
                GalleryPresenter.class,

                ActionEntityFragment.class,
                ActionEntityPresenter.class,
                CreateTripImageFragment.class,
                CreateFeedPostFragment.class,
                CreateFeedPostPresenter.class,
                CreateEntityFragment.class,
                CreateEntityPresenter.class,

                LocationFragment.class,
                LocationPresenter.class,

                SuggestedPhotosCell.class,
                SuggestionPhotoCell.class,

                PhotoPostCreationCell.class,
                PostCreationTextCell.class,
                SubPhotoAttachmentCell.class,
                PostFeedItemDetailsCell.class,

                EditPostFragment.class,
                EditPostPresenter.class,
                EditPhotoFragment.class,
                EditPhotoPresenter.class,
                DescriptionCreatorFragment.class,
                DescriptionCreatorPresenter.class,
                HashtagSuggestionCell.class,

                StatePaginatedRecyclerViewManager.class,

        },
        complete = false,
        library = true
)
public class FeedModule {
    public static final String FEED = Route.FEED.name();
    public static final String NOTIFICATIONS = Route.NOTIFICATIONS.name();

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideFeedComponent() {
        return new ComponentDescription(FEED, R.string.feed_title,
                R.string.feed_title, R.drawable.ic_feed, FeedFragment.class);
    }

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideNotificationComponent() {
        return new ComponentDescription(NOTIFICATIONS, R.string.notifications_title,
                R.string.notifications_title, R.drawable.ic_notifications, NotificationFragment.class);
    }

    @Provides
    FeedEntityContentFragmentFactory provideFeedEntityContentFragmentFactory(SessionHolder<UserSession> sessionHolder) {
        return new FeedEntityContentFragmentFactory(sessionHolder);
    }

    @Provides
    FeedActionPanelViewActionHandler provideFeedActionPanelViewActionHandler(Router router, @Global EventBus eventBus, Presenter.TabletAnalytic tabletAnalytic) {
        return new FeedActionPanelViewActionHandler(router, eventBus);
    }

    @Provides
    FragmentWithFeedDelegate provideFragmentWithFeedDelegate(Router router) {
        return new FragmentWithFeedDelegate(router);
    }
}
