package com.worldventures.dreamtrips.social.ui.feed.view.fragment;

import android.os.Bundle;

import com.worldventures.core.ui.util.SoftInputUtil;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;

import com.worldventures.dreamtrips.social.ui.feed.bundle.FeedItemDetailsBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.VideoFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedItemDetailsPresenter;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.BucketFeedItemDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.PhotoFeedItemDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.PostFeedItemDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.TripFeedItemDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.VideoFeedItemDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.delegate.FeedCellDelegate;

@Layout(R.layout.fragment_comments_with_details)
public class FeedItemDetailsFragment extends FeedDetailsFragment<FeedItemDetailsPresenter, FeedItemDetailsBundle> implements FeedItemDetailsPresenter.View {

   @Override
   protected FeedItemDetailsPresenter createPresenter(Bundle savedInstanceState) {
      return new FeedItemDetailsPresenter(getArgs().getFeedItem());
   }

   @Override
   protected void registerCells() {
      adapter.registerCell(PostFeedItem.class, PostFeedItemDetailsCell.class);
      adapter.registerCell(BucketFeedItem.class, BucketFeedItemDetailsCell.class);
      adapter.registerCell(PhotoFeedItem.class, PhotoFeedItemDetailsCell.class);
      adapter.registerCell(TripFeedItem.class, TripFeedItemDetailsCell.class);
      adapter.registerCell(VideoFeedItem.class, VideoFeedItemDetailsCell.class);

      BaseFeedCell.FeedCellDelegate delegate = new FeedCellDelegate(getPresenter());
      adapter.registerDelegate(PhotoFeedItem.class, delegate);
      adapter.registerDelegate(TripFeedItem.class, delegate);
      adapter.registerDelegate(BucketFeedItem.class, delegate);
      adapter.registerDelegate(PostFeedItem.class, delegate);
      adapter.registerDelegate(VideoFeedItem.class, delegate);
   }

   @Override
   public void updateItem(FeedItem feedItem) {
      updateFeedItem(feedItem);
   }

   @Override
   public void onPause() {
      super.onPause();
      /* Hiding keyboard saves app from crash on Samsung Tablets. Here is crash log from samsung tab
        Fatal Exception: java.lang.NullPointerException
            at android.widget.LinearLayout.layoutHorizontal(LinearLayout.java:1629)
            at android.widget.LinearLayout.onLayout(LinearLayout.java:1442)
            at android.view.View.layout(View.java:15746)
      */
      SoftInputUtil.hideSoftInputMethod(getActivity());
   }
}
