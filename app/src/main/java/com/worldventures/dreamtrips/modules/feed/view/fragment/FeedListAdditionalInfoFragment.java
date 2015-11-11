package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.feed.bundle.PostBundle;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedListAdditionalInfoPresenter;
import com.worldventures.dreamtrips.modules.feed.view.util.CirclesFilterPopupWindow;
import com.worldventures.dreamtrips.modules.feed.view.util.NestedLinearLayoutManager;
import com.worldventures.dreamtrips.modules.friends.bundle.FriendGlobalSearchBundle;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.view.cell.FeedFriendCell;
import com.worldventures.dreamtrips.modules.profile.view.ProfileViewUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.support.v7.widget.RecyclerView.LayoutManager;
import static android.support.v7.widget.RecyclerView.OnScrollListener;

@Layout(R.layout.fragment_feed_list_additional_info)
public class FeedListAdditionalInfoFragment extends FeedItemAdditionalInfoFragment<FeedListAdditionalInfoPresenter> implements FeedListAdditionalInfoPresenter.View {

    @InjectView(R.id.account_type)
    TextView accountType;
    @InjectView(R.id.dt_points)
    TextView dtPoints;
    @InjectView(R.id.rovia_bucks)
    TextView roviaBucks;
    @InjectView(R.id.details)
    ViewGroup details;
    @InjectView(R.id.close_friends)
    ViewGroup closeFriends;
    @InjectView(R.id.lv_close_friends)
    EmptyRecyclerView lvCloseFriends;
    @InjectView(R.id.circle_title)
    TextView circleTitle;
    @InjectView(R.id.feed_friend_empty_view)
    View emptyView;

    CirclesFilterPopupWindow filterPopupWindow;
    boolean loading;
    int previousTotal;

    BaseArrayListAdapter<User> adapter;

    OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int totalItemCount = recyclerView.getLayoutManager().getItemCount();
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
            int pageSize = getPresenter().getPageSize();
            if (!loading && lastVisibleItemPosition >= totalItemCount - 1 && totalItemCount % pageSize == 0) {
                getPresenter().loadFriends(adapter.getCount() / pageSize + 1);
                loading = true;
            }
        }
    };

    @Override
    protected FeedListAdditionalInfoPresenter createPresenter(Bundle savedInstanceState) {
        return new FeedListAdditionalInfoPresenter(getArgs());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        LayoutManager layoutManager = new NestedLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new BaseArrayListAdapter<>(getContext(), this);
        adapter.registerCell(User.class, FeedFriendCell.class);

        if (lvCloseFriends != null) {
            lvCloseFriends.setEmptyView(emptyView);
            lvCloseFriends.addOnScrollListener(onScrollListener);
            lvCloseFriends.setLayoutManager(layoutManager);
            lvCloseFriends.setAdapter(adapter);
        }
    }

    @Override
    public void setFriends(@NonNull List<User> friends) {
        closeFriends.setVisibility(View.VISIBLE);
        adapter.clear();
        adapter.addItems(friends);
    }

    @Override
    public void addFriends(@NonNull List<User> friends) {
        adapter.addItems(friends);
    }

    @Override
    public void removeFriend(@NonNull User friend) {
        adapter.remove(friend);
    }

    @Override
    public void showCirclePicker(@NonNull List<Circle> circles, Circle activeCircle) {
        if (filterPopupWindow == null || filterPopupWindow.dismissPassed()) {
            filterPopupWindow = new CirclesFilterPopupWindow(circleTitle.getContext());
            filterPopupWindow.setCircles(circles);
            filterPopupWindow.setAnchorView(circleTitle);
            filterPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
                filterPopupWindow.dismiss();
                getPresenter().circlePicked(circles.get(position));
                circleTitle.setText(circles.get(position).getName());
            });
            filterPopupWindow.show();
            filterPopupWindow.setCheckedCircle(activeCircle);
        }
    }

    @Override
    public void setCurrentCircle(Circle currentCircle) {
        circleTitle.setText(currentCircle.getName());
    }

    private void openPost() {
        showPostContainer();

        fragmentCompass.removePost();
        fragmentCompass.disableBackStack();
        fragmentCompass.setContainerId(R.id.container_details_floating);

        NavigationBuilder.create()
                .with(fragmentCompass)
                .attach(Route.POST_CREATE);
    }

    private void openSharePhoto() {
        showPostContainer();

        fragmentCompass.removePost();
        fragmentCompass.disableBackStack();
        fragmentCompass.setContainerId(R.id.container_details_floating);

        NavigationBuilder.create()
                .with(fragmentCompass)
                .data(new PostBundle(null, PostBundle.PHOTO))
                .attach(Route.POST_CREATE);

    }

    protected void openSearch() {
        NavigationBuilder.create().with(activityRouter)
                .data(new FriendGlobalSearchBundle(""))
                .move(Route.FRIEND_SEARCH);
    }

    protected void showPostContainer() {
        View container = ButterKnife.findById(getActivity(), R.id.container_details_floating);
        if (container != null) container.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.share_post)
    protected void onPostClicked() {
        openPost();
    }

    @OnClick(R.id.share_photo)
    protected void onSharePhotoClick() {
        openSharePhoto();
    }

    @OnClick(R.id.global)
    protected void onGlobalSearchClicked() {
        openSearch();
    }

    @OnClick(R.id.circle_title)
    protected void onCircleFilterClicked() {
        getPresenter().onCircleFilterClicked();
    }

    @Override
    protected void setUser(User user) {
        super.setUser(user);
        details.setVisibility(View.VISIBLE);
        viewProfile.setVisibility(View.GONE);
        accountType.setText(user.getCompany());
        dtPoints.setText(df.format(user.getDreamTripsPoints()));
        roviaBucks.setText(df.format(user.getRoviaBucks()));

        ProfileViewUtils.setUserStatus(user, accountType, companyName.getResources());
    }

}

