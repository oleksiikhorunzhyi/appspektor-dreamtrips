package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.annotations.Layout;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.modules.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedItemDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.BucketFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PostFeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.TripFeedItemDetailsCell;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.fragment_comments_with_details)
public class FeedItemDetailsFragment extends FeedDetailsFragment<FeedItemDetailsPresenter> implements FeedItemDetailsPresenter.View {

    @Optional @InjectView(R.id.comments_additional_info_container) ViewGroup additionalContainer;

    @Override
    protected FeedItemDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new FeedItemDetailsPresenter(getArgs().getFeedItem());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        if (!getArgs().isShowAdditionalInfo()) {
            if (additionalContainer != null) {
                additionalContainer.setVisibility(View.GONE);
            }
        }
        recyclerView.post(() -> {
            if (recyclerView != null) recyclerView.scrollBy(0, 1);
        });
    }

    @Override
    protected void registerCells() {
        adapter.registerCell(PostFeedItem.class, PostFeedItemDetailsCell.class);
        adapter.registerCell(BucketFeedItem.class, BucketFeedItemDetailsCell.class);
        adapter.registerCell(PhotoFeedItem.class, PhotoFeedItemDetailsCell.class);
        adapter.registerCell(TripFeedItem.class, TripFeedItemDetailsCell.class);
    }

    @Override
    public void setFeedItem(FeedItem feedItem) {
        super.setFeedItem(feedItem);
        //todo until Trip becomes as all normal entities
        if (feedItem instanceof TripFeedItem) {
            if (additionalContainer != null) additionalContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void showAdditionalInfo(User user) {
        if (isShowAdditionalInfo()) {
            router.moveTo(Route.FEED_ITEM_ADDITIONAL_INFO, NavigationConfigBuilder.forFragment()
                    .backStackEnabled(false)
                    .fragmentManager(getChildFragmentManager())
                    .containerId(R.id.comments_additional_info_container)
                    .data(new FeedAdditionalInfoBundle(user))
                    .build());
        }
    }

    @Override
    public void updateItem(FeedItem feedItem) {
        updateFeedItem(feedItem);
    }

    private boolean isShowAdditionalInfo() {
        return getActivity().getSupportFragmentManager().findFragmentById(R.id.comments_additional_info_container) == null
                && getArgs().isShowAdditionalInfo();
    }

    @Override
    public void onPause() {
        super.onPause();
        /* Hiding keyboard saves app from crash on Samsung Tablets. Here is crash log from samsung tab
        Fatal Exception: java.lang.NullPointerException
            at android.widget.LinearLayout.layoutHorizontal(LinearLayout.java:1629)
            at android.widget.LinearLayout.onLayout(LinearLayout.java:1442)
            at android.view.View.layout(View.java:15746)*/
        SoftInputUtil.hideSoftInputMethod(getActivity());
    }
}
