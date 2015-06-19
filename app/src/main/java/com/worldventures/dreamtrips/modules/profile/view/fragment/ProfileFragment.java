package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.expandablelayout.library.ExpandableLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.kbeanie.imagechooser.api.ChooserType;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.profile.presenter.ProfilePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.PickImageDialog;

import butterknife.InjectView;
import butterknife.OnClick;
import icepick.Icicle;
import timber.log.Timber;


@Layout(R.layout.fragment_profile)
@MenuResource(R.menu.profile_fragment)
public class ProfileFragment extends BaseFragment<ProfilePresenter>
        implements ProfilePresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.user_cover)
    protected SimpleDraweeView userCover;

    @InjectView(R.id.user_photo)
    protected SimpleDraweeView userPhoto;
    @InjectView(R.id.cover)
    protected ImageView cover;
    @InjectView(R.id.avatar)
    protected ImageView avatar;
    @InjectView(R.id.user_name)
    protected TextView userName;
    @InjectView(R.id.et_date_of_birth)
    protected DTEditText dateOfBirth;
    @InjectView(R.id.pb)
    protected ProgressBarCircularIndeterminate progressBar;
    @InjectView(R.id.trip_images)
    protected TextView tripImages;
    @InjectView(R.id.dream_trips)
    protected TextView trips;
    @InjectView(R.id.update_info)
    protected TextView updateInfo;
    @InjectView(R.id.user_status)
    protected TextView userStatus;
    @InjectView(R.id.add_friend)
    protected TextView addFriend;
    @InjectView(R.id.bucket_list)
    protected TextView buckets;
    @InjectView(R.id.sv)
    protected ScrollView sv;
    @InjectView(R.id.friend_request)
    protected ViewGroup friendRequest;
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
    @InjectView(R.id.accept)
    protected ButtonRectangle accept;
    @InjectView(R.id.reject)
    protected ButtonRectangle reject;
    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout swipeContainer;
    @InjectView(R.id.expandable_info)
    protected ExpandableLayout info;
    @InjectView(R.id.more)
    protected ViewGroup more;
    private PickImageDialog pid;

    @Icicle
    int pidTypeShown;
    @Icicle
    String filePath;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        layoutConfiguration();
    }

    private void layoutConfiguration() {
        int padding = getResources().getDimensionPixelSize(R.dimen.spacing_normal);
        accept.getTextView().setPadding(padding, 0, padding, 0);
        reject.setTextColor(getResources().getColor(R.color.black_semi_transparent));
        swipeContainer.setOnRefreshListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).makeActionBarTransparent(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).makeActionBarTransparent(false);
    }

    @Override
    protected ProfilePresenter createPresenter(Bundle savedInstanceState) {
        return new ProfilePresenter();
    }

    @OnClick(R.id.bucket_list)
    public void onBucketListClicked() {
        getPresenter().openBucketList();
    }

    @OnClick(R.id.trip_images)
    public void onTripImageClicked() {
        getPresenter().openTripImages();
    }

    @OnClick(R.id.user_photo)
    public void onPhotoClick() {
        getPresenter().photoClicked();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem logout = menu.findItem(R.id.item_logout);
        logout.setVisible(getPresenter().isCurrentUserProfile());
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
    public void avatarProgressVisible(boolean visible) {
        progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
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

    @OnClick(R.id.user_cover)
    public void onCoverClick() {
        getPresenter().coverClicked();
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
    public void openCoverPicker() {
        this.pid = new PickImageDialog(getActivity(), this);
        this.pid.setTitle(getString(R.string.profile_select_cover_header));
        this.pid.setCallback(getPresenter().provideCoverChooseCallback());
        this.pid.show();
    }

    @Override
    public void openAvatarPicker() {
        pidTypeShown = PickImageDialog.REQUEST_PICK_PICTURE;
        this.pid = new PickImageDialog(getActivity(), this);
        this.pid.setTitle(getString(R.string.profile_select_avatar_header));
        this.pid.setRequestTypes(ChooserType.REQUEST_PICK_PICTURE);
        this.pid.setCallback(getPresenter().provideAvatarChooseCallback());
        this.pid.show();
        filePath = pid.getFilePath();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (pidTypeShown != 0) {
            this.pid = new PickImageDialog(getActivity(), this);
            this.pid.setCallback(getPresenter().provideAvatarChooseCallback());
            this.pid.setChooserType(pidTypeShown);
            this.pid.setFilePath(filePath);
            pidTypeShown = 0;
        }
        //
        if (this.pid != null)
            this.pid.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_logout:
                showLogoutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
    public void hideAccountContent() {
        cover.setVisibility(View.GONE);
        avatar.setVisibility(View.GONE);
    }

    @Override
    public void showAccountContent() {
        cover.setVisibility(View.GONE);
        avatar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAddFriend() {
        addFriend.setVisibility(View.VISIBLE);
        updateInfo.setVisibility(View.GONE);
    }

    @Override
    public void showUpdateProfile() {
        addFriend.setVisibility(View.GONE);
        updateInfo.setVisibility(View.VISIBLE);
    }

    @Override
    public void setIsFriend(boolean isFriend) {
        addFriend.setCompoundDrawablesWithIntrinsicBounds(isFriend
                        ? R.drawable.friend_added
                        : R.drawable.add_friend,
                0, 0, 0);
    }

    @Override
    public void showFriendRequest() {
        friendRequest.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFriendRequest() {
        friendRequest.setVisibility(View.GONE);
    }

    @Override
    public void setRoviaBucks(int count) {
        roviaBucks.setText(Html.fromHtml(getString(R.string.profile_rovia_bucks, count)));
    }

    @Override
    public void setDreamTripPoints(int count) {
        dtPoints.setText(Html.fromHtml(getString(R.string.profile_dt_points, count)));
    }

    @Override
    public void hideBalance() {
        userBalance.setVisibility(View.GONE);
    }

    @Override
    public void showBalance() {
        userBalance.setVisibility(View.VISIBLE);
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

    @OnClick(R.id.add_friend)
    void onAddFriend() {

    }

    @OnClick(R.id.update_info)
    void onUpdateInfo() {

    }

    @Override
    public void onRefresh() {
        getPresenter().onRefresh();
    }

    @Override
    public void startLoading() {
        swipeContainer.post(() -> {
            if (swipeContainer != null) swipeContainer.setRefreshing(true);
        });
    }

    @Override
    public void finishLoading() {
        swipeContainer.setRefreshing(false);
    }

    private void showLogoutDialog() {
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.logout_dialog_title))
                .content(getString(R.string.logout_dialog_message))
                .positiveText(getString(R.string.logout_dialog_positive_btn))
                .negativeText(getString(R.string.logout_dialog_negative_btn))
                .positiveColorRes(R.color.theme_main_darker)
                .negativeColorRes(R.color.theme_main_darker)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        getPresenter().logout();
                    }
                })
                .show();
    }

}
