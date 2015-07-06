package com.worldventures.dreamtrips.modules.feed;

import com.worldventures.dreamtrips.modules.feed.model.FeedAvatarEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedBucketEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedCoverEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedPhotoEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedTripEventModel;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedAvatarEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedBucketEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedCoverEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedPhotoEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedTripEventCell;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedFragment;

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
                FeedBucketEventModel.class,

                FeedPresenter.class,
                FeedFragment.class
        },
        complete = false,
        library = true
)
public class FeedModule {
}
