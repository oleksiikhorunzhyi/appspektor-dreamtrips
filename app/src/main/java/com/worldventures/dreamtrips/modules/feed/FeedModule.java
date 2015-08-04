package com.worldventures.dreamtrips.modules.feed;

import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;

import com.worldventures.dreamtrips.modules.feed.model.FeedAvatarEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedBucketEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedCoverEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedPhotoEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedPostEventModel;
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

@Module(
        injects = {
                FeedAvatarEventCell.class,
                FeedCoverEventCell.class,
                FeedTripEventCell.class,
                FeedTripCommentCell.class,
                FeedPhotoEventCell.class,
                FeedPhotoCommentCell.class,
                FeedBucketEventCell.class,
                FeedBucketCommentCell.class,
                LoadMoreCell.class,
                FeedPhotoEventCell.class,
                FeedPostEventCell.class,
                FeedUndefinedEventCell.class,

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
                ParallaxRecyclerAdapter.class,
        },
        complete = false,
        library = true
)
public class FeedModule {
}
