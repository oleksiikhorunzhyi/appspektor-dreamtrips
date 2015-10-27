package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.bundle.PostBundle;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedListAdditionalInfoPresenter;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

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
        feedTabletViewManager.setOnCirclePicked((c) -> getPresenter().circlePicked(c));
        feedTabletViewManager.setRequestMoreUsersListener((page, circle) -> getPresenter().loadFriends(page, circle));
        feedTabletViewManager.setOnSharePhotoClick(this::openSharePhoto);
    }


    @Override
    public void addCloseFriends(List<User> friends) {
        feedTabletViewManager.setCloseFriends(friends, this);
    }

    @Override
    public void addFriends(List<User> friends) {
        feedTabletViewManager.addCloseFriends(friends);
    }

    @Override
    public void setCircles(List<Circle> circles) {
        circles.add(0, Circle.all(getResources().getString(R.string.show_all)));
        feedTabletViewManager.setCircles(circles, 0);
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

    public void openSharePhoto() {
        showPostContainer();

        fragmentCompass.removePost();
        fragmentCompass.disableBackStack();
        fragmentCompass.setContainerId(R.id.container_details_floating);

        NavigationBuilder.create()
                .with(fragmentCompass)
                .data(new PostBundle(null, PostBundle.PHOTO))
                .attach(Route.POST_CREATE);

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

