package com.worldventures.dreamtrips.modules.feed;

import com.techery.spares.module.qualifier.Global;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.presenter.BaseCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.EditCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedItemAdditionalInfoPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedItemDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedListAdditionalInfoPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.GalleryPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.NotificationPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.PhotoDetailsFeedPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.PostEditPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.PostPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.TextualPostDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.AttachPhotoCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.BucketFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.CommentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedEntityDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedItemCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.LoadMoreCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoGalleryCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PostFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.TripFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.UndefinedFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.notification.NotificationCell;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CommentableFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.EditCommentFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedItemAdditionalInfoFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedItemDetailsFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedListAdditionalInfoFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.DtGalleryFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.NotificationFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.PhotoDetailsFeedFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.PostFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.TextualPostDetailsFragment;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedActionPanelViewActionHandler;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedEntityContentFragmentFactory;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module(
        injects = {
                TripFeedItemDetailsCell.class,
                FeedPresenter.class,
                FeedFragment.class,
                BucketFeedItemDetailsCell.class,
                LoadMoreCell.class,
                PhotoFeedItemDetailsCell.class,
                PostFeedItemDetailsCell.class,
                UndefinedFeedItemDetailsCell.class,

                AttachPhotoCell.class,
                PhotoGalleryCell.class,

                EditCommentPresenter.class,

                CommentableFragment.class,
                ComponentPresenter.class,
                FeedItemDetailsFragment.class,
                FeedItemDetailsPresenter.class,
                CommentCell.class,
                BaseCommentPresenter.class,
                PostPresenter.class,
                PostFragment.class,
                PostEditPresenter.class,

                NotificationFragment.class,
                NotificationPresenter.class,
                NotificationCell.class,
                NotificationFragment.NotificationAdapter.class,

                TextualPostDetailsFragment.class,
                TextualPostDetailsPresenter.class,

                PhotoDetailsFeedFragment.class,
                PhotoDetailsFeedPresenter.class,

                FeedItemDetailsCell.class,
                FeedItemCell.class,
                BaseFeedCell.class,
                FeedEntityDetailsCell.class,
                TripFeedItemDetailsCell.class,

                FeedListAdditionalInfoFragment.class,
                FeedListAdditionalInfoPresenter.class,
                FeedItemAdditionalInfoFragment.class,
                FeedItemAdditionalInfoPresenter.class,

                EditCommentFragment.class,
                EditCommentPresenter.class,

                DtGalleryFragment.class,
                GalleryPresenter.class,

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
}
