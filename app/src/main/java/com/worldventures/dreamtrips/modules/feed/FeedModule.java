package com.worldventures.dreamtrips.modules.feed;

import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.BaseCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.EditCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedEntityDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.NotificationPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.PostEditPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.PostPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.TextualPostDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.CommentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedBucketEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedPhotoEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedPostCommentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedPostEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedTripEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedUndefinedEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.LoadMoreCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.comment.FeedBucketCommentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.comment.FeedPhotoCommentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.comment.FeedTripCommentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.notification.NotificationCell;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CommentsFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedEntityDetailsFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.NotificationFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.PostFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.TextualPostDetailsFragment;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedActionPanelViewActionHandler;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedEntityContentFragmentFactory;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module(
        injects = {
                FeedTripEventCell.class,
                FeedPresenter.class,
                FeedFragment.class,
                FeedTripCommentCell.class,
                FeedPhotoEventCell.class,
                FeedPhotoCommentCell.class,
                FeedBucketEventCell.class,
                FeedBucketCommentCell.class,
                FeedPostCommentCell.class,
                LoadMoreCell.class,
                FeedPhotoEventCell.class,
                FeedPostEventCell.class,
                FeedUndefinedEventCell.class,

                EditCommentPresenter.class,

                CommentsFragment.class,
                ComponentPresenter.class,
                CommentCell.class,
                BaseCommentPresenter.class,
                PostPresenter.class,
                PostFragment.class,
                PostEditPresenter.class,

                NotificationFragment.class,
                NotificationPresenter.class,
                NotificationCell.class,
                NotificationFragment.NotificationAdapter.class,
                FeedEntityDetailsFragment.class,
                FeedEntityDetailsPresenter.class,

                TextualPostDetailsFragment.class,
                TextualPostDetailsPresenter.class
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
    FeedEntityContentFragmentFactory provideFeedEntityContentFragmentFactory() {
        return new FeedEntityContentFragmentFactory();
    }

    @Provides
    FeedActionPanelViewActionHandler provideFeedActionPanelViewActionHandler(ActivityRouter activityRouter, @Global EventBus eventBus) {
        return new FeedActionPanelViewActionHandler(activityRouter, eventBus);
    }

}
