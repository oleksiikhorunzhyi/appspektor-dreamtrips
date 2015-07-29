package com.worldventures.dreamtrips.modules.feed;

import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.feed.model.FeedAvatarEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedBucketEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedCoverEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedPhotoEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedTripEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedUndefinedEventModel;
import com.worldventures.dreamtrips.modules.feed.presenter.BaseCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.CommentsActivityPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedPresenter;
import com.worldventures.dreamtrips.modules.feed.view.activity.CommentsActivity;

import com.worldventures.dreamtrips.modules.feed.presenter.PostPresenter;
import com.worldventures.dreamtrips.modules.feed.view.adapter.ParallaxRecyclerAdapter;
import com.worldventures.dreamtrips.modules.feed.view.cell.CommentCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedAvatarEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedBucketEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedCoverEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedPhotoEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedTripEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedUndefinedEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.LoadMoreCell;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CommentsFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedFragment;
import com.worldventures.dreamtrips.modules.feed.view.fragment.PostFragment;

import dagger.Module;

@Module(
        injects = {
                FeedAvatarEventCell.class,
                FeedAvatarEventModel.class,

                FeedCoverEventCell.class,
                FeedCoverEventModel.class,

                FeedTripEventCell.class,
                FeedTripEventModel.class,

                FeedPhotoEventCell.class,
                FeedPhotoEventModel.class,

                FeedBucketEventCell.class,
                LoadMoreCell.class,
                FeedBucketEventModel.class,

                FeedUndefinedEventCell.class,
                FeedUndefinedEventModel.class,

                FeedPresenter.class,
                FeedFragment.class,
                ParallaxRecyclerAdapter.class,

                CommentsFragment.class,
                ComponentPresenter.class,
                CommentCell.class,
                CommentsActivity.class,
                CommentsActivityPresenter.class,
                BaseCommentPresenter.class,
                PostPresenter.class,
                PostFragment.class,
                ParallaxRecyclerAdapter.class
        },
        complete = false,
        library = true
)
public class FeedModule {
}
