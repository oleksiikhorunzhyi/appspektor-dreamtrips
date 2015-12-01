package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.fragment.FragmentHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedItemDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedItemDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.TripFeedItemDetailsCell;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.fragment_comments_with_details)
public class FeedItemDetailsFragment extends CommentableFragment<FeedItemDetailsPresenter, FeedItemDetailsBundle> implements FeedItemDetailsPresenter.View {

    @Optional
    @InjectView(R.id.comments_additional_info_container)
    ViewGroup additionalContainer;
    @Optional
    @InjectView(R.id.feedDetailsLeftSpace)
    View feedDetailsLeftSpace;
    @Optional
    @InjectView(R.id.feedDetailsRightSpace)
    View feedDetailsRightSpace;

    private FragmentCompass childCompass;

    @Override
    protected FeedItemDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new FeedItemDetailsPresenter(getArgs().getFeedItem());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        if (getArgs().isShowAdditionalInfo()) {
            childCompass = new FragmentCompass(getActivity(), R.id.comments_additional_info_container);
            childCompass.setFragmentManager(getChildFragmentManager());
            childCompass.disableBackStack();
            //
        } else {
            if (additionalContainer != null) {
                additionalContainer.setVisibility(View.GONE);
            }
        }
        adapter.registerCell(PostFeedItem.class, FeedItemDetailsCell.class);
        adapter.registerCell(BucketFeedItem.class, FeedItemDetailsCell.class);
        adapter.registerCell(PhotoFeedItem.class, FeedItemDetailsCell.class);
        adapter.registerCell(TripFeedItem.class, TripFeedItemDetailsCell.class);
    }

    @Override
    public void onDestroyView() {
        FragmentHelper.resetChildFragmentManagerField(this);
        //
        super.onDestroyView();
    }

    @Override
    public void setFeedItem(FeedItem feedItem) {
        adapter.addItem(0, feedItem);
        adapter.notifyItemInserted(0);
        //todo until Trip becomes as all normal entities
        if (feedItem instanceof TripFeedItem) {
            if (additionalContainer != null) additionalContainer.setVisibility(View.GONE);
            if (feedDetailsLeftSpace != null) feedDetailsLeftSpace.setVisibility(View.VISIBLE);
            if (feedDetailsRightSpace != null) feedDetailsRightSpace.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateFeedItem(FeedItem feedItem) {
        adapter.updateItem(feedItem);
    }

    @Override
    public void showAdditionalInfo(User user) {
        if (childCompass != null && childCompass.empty() && getArgs().isShowAdditionalInfo()) {
            NavigationBuilder.create()
                    .with(childCompass)
                    .data(new FeedAdditionalInfoBundle(user))
                    .move(Route.FEED_ITEM_ADDITIONAL_INFO);
        }
    }

    @Override
    protected int getAdditionalItemsCount() {
        return super.getAdditionalItemsCount() + 1;
    }

    @Override
    protected int getLoadMorePosition() {
        return super.getLoadMorePosition() + 1;
    }
}
