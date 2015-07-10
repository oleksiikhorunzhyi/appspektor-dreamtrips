package com.worldventures.dreamtrips.modules.profile.view.custom;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andexert.expandablelayout.library.ExpandableLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ProfileView extends LinearLayout {

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
    @InjectView(R.id.pb_cover)
    protected ProgressBar coverProgressBar;
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
    @InjectView(R.id.user_balance)
    protected ViewGroup userBalance;
    @InjectView(R.id.expandable_info)
    protected ExpandableLayout info;
    @InjectView(R.id.more)
    protected ViewGroup more;
    @InjectView(R.id.friend_request_caption)
    protected TextView friendRequestCaption;
    @InjectView(R.id.friend_request)
    protected ViewGroup friendRequest;
    @InjectView(R.id.accept)
    protected AppCompatTextView accept;
    @InjectView(R.id.reject)
    protected AppCompatTextView reject;
    @InjectView(R.id.control_panel)
    protected ViewGroup controlPanel;


    OnClickListener onTripImageClicked;
    OnClickListener onFriendsClicked;
    OnClickListener onBucketListClicked;
    OnClickListener onPhotoClick;
    OnClickListener onCoverClick;


    OnClickListener onAcceptRequest;
    OnClickListener onRejectRequest;
    OnClickListener onAddFriend;

    public ProfileView(Context context) {
        this(context, null);
    }

    public ProfileView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProfileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_profile, this);
        if (!isInEditMode()) ButterKnife.inject(this);
    }


    @OnClick({R.id.header, R.id.info, R.id.more, R.id.et_from, R.id.et_enroll, R.id.et_date_of_birth, R.id.et_user_id})
    public void onInfoClick() {
        if (getInfo().isOpened()) {
            getInfo().hide();
            getMore().setVisibility(View.VISIBLE);
        } else {
            getInfo().show();
            getMore().setVisibility(View.INVISIBLE);
        }
    }


    public SimpleDraweeView getUserCover() {
        return userCover;
    }

    public SimpleDraweeView getUserPhoto() {
        return userPhoto;
    }

    public ImageView getCover() {
        return cover;
    }

    public ImageView getAvatar() {
        return avatar;
    }

    public TextView getUserName() {
        return userName;
    }

    public DTEditText getDateOfBirth() {
        return dateOfBirth;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public ProgressBar getCoverProgressBar() {
        return coverProgressBar;
    }

    public TextView getTripImages() {
        return tripImages;
    }

    public TextView getTrips() {
        return trips;
    }

    public TextView getUpdateInfo() {
        return updateInfo;
    }

    public TextView getAddFriend() {
        return addFriend;
    }

    public TextView getUserStatus() {
        return userStatus;
    }

    public TextView getBuckets() {
        return buckets;
    }

    public DTEditText getEtUserId() {
        return etUserId;
    }

    public DTEditText getEtFrom() {
        return etFrom;
    }

    public DTEditText getEtEnroll() {
        return etEnroll;
    }

    public TextView getDtPoints() {
        return dtPoints;
    }

    public TextView getRoviaBucks() {
        return roviaBucks;
    }

    public ViewGroup getUserBalance() {
        return userBalance;
    }

    public ExpandableLayout getInfo() {
        return info;
    }

    public ViewGroup getMore() {
        return more;
    }

    public TextView getFriendRequestCaption() {
        return friendRequestCaption;
    }

    public ViewGroup getFriendRequest() {
        return friendRequest;
    }

    public AppCompatTextView getAccept() {
        return accept;
    }

    public AppCompatTextView getReject() {
        return reject;
    }

    public ViewGroup getControlPanel() {
        return controlPanel;
    }


    @OnClick(R.id.bucket_list)
    protected void onBucketListClicked() {
        if (onBucketListClicked != null) onBucketListClicked.click();
    }

    @OnClick(R.id.trip_images)
    protected void onTripImageClicked() {
        if (onTripImageClicked != null) onTripImageClicked.click();

    }

    @OnClick(R.id.friends)
    protected void onFriendsClick() {
        if (onFriendsClicked != null) onFriendsClicked.click();
    }


    @OnClick(R.id.user_photo)
    protected void onPhotoClick() {
        if (onPhotoClick != null) onPhotoClick.click();
    }

    @OnClick(R.id.user_cover)
    protected void onCoverClick() {
        if (onCoverClick != null) onCoverClick.click();
    }


    @OnClick(R.id.update_info)
    void onUpdateInfo() {
        //TODO
    }

    @OnClick(R.id.accept)
    protected void onAcceptRequest() {
        if (onAcceptRequest != null) onAcceptRequest.click();
    }

    @OnClick(R.id.reject)
    protected void onRejectRequest() {
        if (onRejectRequest != null) onRejectRequest.click();
    }

    @OnClick(R.id.add_friend)
    protected void onAddFriend() {
        if (onAddFriend != null) onAddFriend.click();
    }


    public void setOnBucketListClicked(OnClickListener onBucketListClicked) {
        this.onBucketListClicked = onBucketListClicked;
    }

    public void setOnTripImageClicked(OnClickListener onTripImageClicked) {
        this.onTripImageClicked = onTripImageClicked;
    }

    public void setOnFriendsClicked(OnClickListener onFriendsClicked) {
        this.onFriendsClicked = onFriendsClicked;
    }

    public void setOnPhotoClick(OnClickListener onPhotoClick) {
        this.onPhotoClick = onPhotoClick;
    }

    public void setOnCoverClick(OnClickListener onCoverClick) {
        this.onCoverClick = onCoverClick;
    }

    public void setOnAcceptRequest(OnClickListener onAcceptRequest) {
        this.onAcceptRequest = onAcceptRequest;
    }

    public void setOnRejectRequest(OnClickListener onRejectRequest) {
        this.onRejectRequest = onRejectRequest;
    }

    public void setOnAddFriend(OnClickListener onAddFriend) {
        this.onAddFriend = onAddFriend;
    }

    public interface OnClickListener {
        void click();
    }

}
