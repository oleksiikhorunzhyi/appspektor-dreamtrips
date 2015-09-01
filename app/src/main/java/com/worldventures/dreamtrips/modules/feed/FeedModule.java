package com.worldventures.dreamtrips.modules.feed;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.BaseCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.EditCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.PostPresenter;
import com.worldventures.dreamtrips.modules.feed.view.adapter.ParallaxRecyclerAdapter;
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
import com.worldventures.dreamtrips.modules.feed.view.fragment.CommentsFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.PostFragment;

import dagger.Module;
import dagger.Provides;

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

                ParallaxRecyclerAdapter.class,

                EditCommentPresenter.class,

                CommentsFragment.class,
                ComponentPresenter.class,
                CommentCell.class,
                BaseCommentPresenter.class,
                PostPresenter.class,
                PostFragment.class,
                ParallaxRecyclerAdapter.class,
        },
        complete = false,
        library = true
)
public class FeedModule {
    public static final String FEED = Route.FEED.name();

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideFeedComponent() {
        return new ComponentDescription(FEED, R.string.feed_title,
                R.string.feed_title, R.drawable.ic_feed, FeedFragment.class);
    }

}
