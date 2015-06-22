package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andexert.expandablelayout.library.ExpandableLayout;
import com.badoo.mobile.util.WeakHandler;
import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.profile.presenter.ProfilePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.PickImageDialog;

import java.text.DecimalFormat;

import butterknife.InjectView;
import butterknife.OnClick;
import icepick.Icicle;


public abstract class ProfileFragment<T extends ProfilePresenter> extends BaseFragment<T>
        implements ProfilePresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.user_cover)
    protected SimpleDraweeView userCover;

    @InjectView(R.id.user_photo)
    protected SimpleDraweeView userPhoto;
    @InjectView(R.id.cover_camera)
    protected ImageView cover;
    @InjectView(R.id.avatar_camera)
    protected ImageView avatar;
    @InjectView(R.id.user_name)
    protected TextView userName;
    @InjectView(R.id.et_date_of_birth)
    protected DTEditText dateOfBirth;
    @InjectView(R.id.pb)
    protected ProgressBar progressBar;
    @InjectView(R.id.trip_images)
    protected TextView tripImages;
    @InjectView(R.id.dream_trips)
    protected TextView trips;
    @InjectView(R.id.update_info)
    protected TextView updateInfo;
    @InjectView(R.id.add_friend)
    protected TextView addFriend;
    @InjectView(R.id.user_status)
    protected TextView userStatus;
    @InjectView(R.id.bucket_list)
    protected TextView buckets;
    @InjectView(R.id.et_user_id)
    protected DTEditText etUserId;
    @InjectView(R.id.et_from)
    protected DTEditText etFrom;
    @InjectView(R.id.et_enroll)
    protected DTEditText etEnroll;
    @InjectView(R.id.dt_points)
    protected TextView dtPoints;
    @InjectView(R.id.rovia_bucks)
    protected TextView roviaBucks;
    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout swipeContainer;
    @InjectView(R.id.user_balance)
    protected ViewGroup userBalance;

    @InjectView(R.id.expandable_info)
    protected ExpandableLayout info;
    @InjectView(R.id.more)
    protected ViewGroup more;
    private PickImageDialog pid;

    @Icicle
    String filePath;

    private WeakHandler weakHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weakHandler = new WeakHandler();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        layoutConfiguration();
    }

    private void layoutConfiguration() {
        swipeContainer.setOnRefreshListener(this);

    }

    @OnClick(R.id.bucket_list)
    public void onBucketListClicked() {
        getPresenter().openBucketList();
    }

    @OnClick(R.id.trip_images)
    public void onTripImageClicked() {
        getPresenter().openTripImages();
    }

    @OnClick(R.id.friends)
    public void onFriendsClick() {
        getPresenter().openFriends();
    }

    @Override
    public void setAvatarImage(Uri uri) {
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> {
                if (uri != null) {
                    this.userPhoto.setImageURI(uri);
                }
            });
    }

    @Override
    public void setCoverImage(Uri uri) {
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> {
                if (uri != null) {
                    this.userCover.setImageURI(uri);
                }
            });
    }

    @Override
    public void setDateOfBirth(String format) {
        dateOfBirth.setText(format);
    }

    @Override
    public void setFrom(String location) {
        etFrom.setText(location);
    }

    @Override
    public void setUserName(String username) {
        userName.setText(username);
    }

    @Override
    public void setUserId(String username) {
        etUserId.setText(username);
    }

    @Override
    public void setEnrollDate(String date) {
        etEnroll.setText(date);
    }

    @OnClick({R.id.header, R.id.info, R.id.more, R.id.et_from, R.id.et_enroll, R.id.et_date_of_birth, R.id.et_user_id})
    public void onInfoClick() {
        if (info.isOpened()) {
            info.hide();
            more.setVisibility(View.VISIBLE);
        } else {
            info.show();
            more.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setTripImagesCount(int count) {
        tripImages.setText(String.format(getString(R.string.profile_trip_images), count));
    }

    @Override
    public void setTripsCount(int count) {
        trips.setText(String.format(getString(R.string.profile_dream_trips), count));
    }

    @Override
    public void setBucketItemsCount(int count) {
        buckets.setText(String.format(getString(R.string.profile_bucket_list), count));
    }

    @Override
    public void setMember() {
        userStatus.setTextColor(getResources().getColor(R.color.white));
        userStatus.setText("");
        userStatus.setCompoundDrawablesWithIntrinsicBounds(0,
                0, 0, 0);
    }

    @Override
    public void setGold() {
        userStatus.setTextColor(getResources().getColor(R.color.golden_user));
        userStatus.setText(R.string.profile_golden);
        userStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.gold_member,
                0, 0, 0);
    }

    @Override
    public void setPlatinum() {
        userStatus.setTextColor(getResources().getColor(R.color.platinum_user));
        userStatus.setText(R.string.profile_platinum);
        userStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.platinum_member,
                0, 0, 0);
    }

    @Override
    public void onRefresh() {
        getPresenter().onRefresh();
    }

    @Override
    public void startLoading() {
        weakHandler.post(() -> {
            if (swipeContainer != null) swipeContainer.setRefreshing(true);
        });
    }

    @Override
    public void finishLoading() {
        weakHandler.post(() -> {
            if (swipeContainer != null) swipeContainer.setRefreshing(false);
        });
    }


}
