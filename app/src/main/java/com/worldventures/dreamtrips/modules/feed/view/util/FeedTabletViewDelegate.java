package com.worldventures.dreamtrips.modules.feed.view.util;

import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.view.cell.FeedFriendCell;
import com.worldventures.dreamtrips.modules.profile.view.ProfileViewUtils;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

public class FeedTabletViewDelegate implements IFeedTabletViewDelegate {

    DecimalFormat df = new DecimalFormat("#0.00");

    @Optional
    @InjectView(R.id.user_cover)
    SimpleDraweeView userCover;
    @Optional
    @InjectView(R.id.user_photo)
    SimpleDraweeView userPhoto;
    @Optional
    @InjectView(R.id.user_name)
    TextView userName;
    @Optional
    @InjectView(R.id.company_name)
    TextView companyName;
    @Optional
    @InjectView(R.id.account_type)
    TextView accountType;
    @Optional
    @InjectView(R.id.dt_points)
    TextView dtPoints;
    @Optional
    @InjectView(R.id.rovia_bucks)
    TextView roviaBucks;
    @Optional
    @InjectView(R.id.share_post)
    TextView sharePost;
    @Optional
    @InjectView(R.id.share_photo)
    TextView sharePhoto;
    @Optional
    @InjectView(R.id.details)
    ViewGroup details;
    @Optional
    @InjectView(R.id.view_profile)
    TextView viewProfile;
    @Optional
    @InjectView(R.id.close_friends)
    ViewGroup closeFriends;
    @Optional
    @InjectView(R.id.lv_close_friends)
    RecyclerView lvCloseFriends;

    @Optional
    @InjectView(R.id.circle_filter)
    ImageView circleFilter;
    @Optional
    @InjectView(R.id.circle_title)
    TextView circleTitle;

    ActionListener onUserClick;
    ActionListener onCreatePostClick;
    CirclePickedListener onCirclePicked;

    RequestMoreUsersListener requestMoreUsersListener;

    CirclesFilterPopupWindow filterPopupWindow;
    List<Circle> circles;
    Circle activeCircle;

    boolean loading;
    int previousTotal;

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int totalItemCount = layoutManager.getItemCount();
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
            if (!loading
                    && lastVisibleItemPosition >= totalItemCount - 1
                    && totalItemCount % 20 == 0) {
                if (requestMoreUsersListener != null) {
                    requestMoreUsersListener.needMore(adapter.getCount() / 20 + 1, activeCircle);
                }
                loading = true;
            }
        }
    };
    private LinearLayoutManager layoutManager;
    private BaseArrayListAdapter<User> adapter;

    public FeedTabletViewDelegate() {
    }

    @Override
    public void setRootView(View view) {
        ButterKnife.inject(this, view);
        if (details != null) details.setVisibility(View.GONE);
        if (viewProfile != null) viewProfile.setVisibility(View.GONE);
        if (closeFriends != null) closeFriends.setVisibility(View.GONE);

    }

    @Override
    public void setUser(User user, boolean withDetails) {
        if (user != null) {
            userPhoto.setImageURI(Uri.parse(user.getAvatar().getThumb()));
            userCover.setImageURI(Uri.parse(user.getBackgroundPhotoUrl()));
            userName.setText(user.getFullName());
            companyName.setText(user.getCompany());
            accountType.setText(user.getCompany());
            dtPoints.setText(df.format(user.getDreamTripsPoints()));
            roviaBucks.setText(df.format(user.getRoviaBucks()));
            ProfileViewUtils.setUserStatus(user, accountType, companyName.getResources());

            details.setVisibility(withDetails ? View.VISIBLE : View.GONE);
            viewProfile.setVisibility(!withDetails ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void setCloseFriends(List<User> friends, Injector injector) {
        if (friends != null && friends.size() > 0) {
            closeFriends.setVisibility(View.VISIBLE);

            adapter = new BaseArrayListAdapter<>(lvCloseFriends.getContext(), injector);
            adapter.registerCell(User.class, FeedFriendCell.class);

            layoutManager = new LinearLayoutManager(lvCloseFriends.getContext(), LinearLayoutManager.VERTICAL, false);
            lvCloseFriends.setLayoutManager(layoutManager);
            lvCloseFriends.setAdapter(adapter);
            lvCloseFriends.addOnScrollListener(onScrollListener);
            adapter.addItems(friends);
        }
    }

    @Override
    public void addCloseFriends(List<User> friends) {
        adapter.addItems(friends);
    }


    @Optional
    @OnClick(R.id.circle_filter)
    public void onActionFilter() {
        if (filterPopupWindow == null || filterPopupWindow.dismissPassed()) {
            actionFilter();
        }
    }

    private void actionFilter() {
        filterPopupWindow = new CirclesFilterPopupWindow(circleFilter.getContext());
        if (circles == null) {
            throw new IllegalStateException("Set circles before filtering");
        }
        filterPopupWindow.setCircles(circles);
        filterPopupWindow.setAnchorView(circleFilter);
        filterPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            filterPopupWindow.dismiss();
            activeCircle = circles.get(position);
            onCirclePicked.onAction(circles.get(position));
            circleTitle.setText(circles.get(position).getName());
        });
        filterPopupWindow.show();
        filterPopupWindow.setCheckedCircle(activeCircle);
    }


    @Optional
    @OnClick({R.id.user_cover, R.id.view_profile})
    void onUserClick() {
        if (onUserClick != null) {
            onUserClick.onAction();
        }
    }

    @Optional
    @OnClick(R.id.share_post)
    void onPostClicked() {
        if (onCreatePostClick != null) {
            onCreatePostClick.onAction();
        }
    }

    @Optional
    @OnClick(R.id.share_photo)
    void onSharePhotoClick() {
        //TODO open share photo. Now it's dummy
        if (onCreatePostClick != null) {
            onCreatePostClick.onAction();
        }
    }

    @Override
    public void setOnUserClick(ActionListener onUserClick) {
        this.onUserClick = onUserClick;
    }

    @Override
    public void setOnCreatePostClick(ActionListener onCreatePostClick) {
        this.onCreatePostClick = onCreatePostClick;
    }

    @Override
    public void setCircles(List<Circle> circles, int defaultCircleIndex) {
        if (defaultCircleIndex > circles.size()) {
            throw new IllegalArgumentException();
        }
        this.circleTitle.setText(circles.get(defaultCircleIndex).getName());
        this.circles = circles;
        this.activeCircle = circles.get(defaultCircleIndex);

    }

    public void setRequestMoreUsersListener(RequestMoreUsersListener requestMoreUsersListener) {
        this.requestMoreUsersListener = requestMoreUsersListener;
    }

    @Override
    public void setOnCirclePicked(CirclePickedListener onCirclePicked) {
        this.onCirclePicked = onCirclePicked;
    }

}
