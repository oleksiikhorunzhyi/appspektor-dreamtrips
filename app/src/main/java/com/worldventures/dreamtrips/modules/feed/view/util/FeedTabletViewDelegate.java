package com.worldventures.dreamtrips.modules.feed.view.util;

import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.view.cell.CloseFriendCell;
import com.worldventures.dreamtrips.modules.membership.view.util.DividerItemDecoration;
import com.worldventures.dreamtrips.modules.membership.view.util.WrapContentLinearLayoutManager;
import com.worldventures.dreamtrips.modules.profile.view.ProfileViewUtils;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

public class FeedTabletViewDelegate implements IFeedTabletViewDelegate {
    private static final int CLOSE_FRIENDS_COUNT = 5;

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

    ViewClickListener onUserClick;
    ViewClickListener onCreatePostClick;
    ViewClickListener onFriendsMoreClick;

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

            BaseArrayListAdapter<User> adapter = new BaseArrayListAdapter<>(lvCloseFriends.getContext(), injector);
            adapter.registerCell(User.class, CloseFriendCell.class);

            lvCloseFriends.setLayoutManager(new WrapContentLinearLayoutManager(lvCloseFriends.getContext(), LinearLayoutManager.VERTICAL, false));
            lvCloseFriends.addItemDecoration(new DividerItemDecoration(lvCloseFriends.getContext(), DividerItemDecoration.VERTICAL_LIST));
            lvCloseFriends.setAdapter(adapter);

            adapter.addItems(Queryable.from(friends).take(CLOSE_FRIENDS_COUNT).toList());
        }
    }


    @Optional
    @OnClick({R.id.user_cover, R.id.view_profile})
    void onUserClick() {
        if (onUserClick != null) {
            onUserClick.onClick();
        }
    }

    @Optional
    @OnClick(R.id.share_post)
    void onPostClicked() {
        if (onCreatePostClick != null) {
            onCreatePostClick.onClick();
        }
    }

    @Optional
    @OnClick(R.id.tv_see_more)
    void onFriendsMoreClick() {
        if (onFriendsMoreClick != null) {
            onFriendsMoreClick.onClick();
        }
    }


    @Optional
    @OnClick(R.id.share_photo)
    void onSharePhotoClick() {
        //TODO open share photo. Now it's dummy
        if (onCreatePostClick != null) {
            onCreatePostClick.onClick();
        }
    }


    @Override
    public void setOnUserClick(ViewClickListener onUserClick) {
        this.onUserClick = onUserClick;
    }

    @Override
    public void setOnCreatePostClick(ViewClickListener onCreatePostClick) {
        this.onCreatePostClick = onCreatePostClick;
    }

    @Override
    public void setOnFriendsMoreClick(ViewClickListener onFriendsMoreClick) {
        this.onFriendsMoreClick = onFriendsMoreClick;
    }

    public interface ViewClickListener {
        void onClick();
    }

}
