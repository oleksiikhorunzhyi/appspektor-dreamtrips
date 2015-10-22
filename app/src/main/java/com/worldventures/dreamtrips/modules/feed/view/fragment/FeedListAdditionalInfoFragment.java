package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedListAdditionalInfoPresenter;

import java.util.List;

import butterknife.ButterKnife;

@Layout(R.layout.fragment_feed_list_additional_info)
public class FeedListAdditionalInfoFragment extends FeedItemAdditionalInfoFragment<FeedListAdditionalInfoPresenter> implements FeedListAdditionalInfoPresenter.View {

    @Override
    protected FeedListAdditionalInfoPresenter createPresenter(Bundle savedInstanceState) {
        return new FeedListAdditionalInfoPresenter(getArgs());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        feedTabletViewManager.setOnCreatePostClick(this::openPost);
        feedTabletViewManager.setOnFriendsMoreClick(this::openFriends);
    }


    @Override
    public void setupCloseFriends(List<User> friends) {
        feedTabletViewManager.setCloseFriends(friends, this);
    }

    public void openPost() {
        showPostContainer();

        fragmentCompass.removePost();
        fragmentCompass.disableBackStack();
        fragmentCompass.setContainerId(R.id.container_details_floating);

        NavigationBuilder.create()
                .with(fragmentCompass)
                .attach(Route.POST_CREATE);
    }

    public void openFriends() {
        NavigationBuilder.create()
                .with(activityRouter)
                .move(Route.FRIENDS);
    }

    protected void showPostContainer() {
        View container = ButterKnife.findById(getActivity(), R.id.container_details_floating);
        if (container != null) container.setVisibility(View.VISIBLE);
    }

    @Override
    protected boolean isFullInfoShown() {
        return true;
    }
}

