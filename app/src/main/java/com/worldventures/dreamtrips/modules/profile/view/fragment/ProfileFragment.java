package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.profile.presenter.ProfilePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.PickImageDialog;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import timber.log.Timber;

import static com.worldventures.dreamtrips.core.utils.ViewUtils.getMinSideSize;

@Layout(R.layout.fragment_profile)
@MenuResource(R.menu.profile_fragment)
public class ProfileFragment extends BaseFragment<ProfilePresenter>
        implements ProfilePresenter.View {

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
    @InjectView(R.id.bucket_list)
    protected TextView buckets;
    @Optional
    @InjectView(R.id.sv)
    protected ScrollView sv;
    @InjectView(R.id.et_user_id)
    protected DTEditText etUserId;
    @InjectView(R.id.et_from)
    protected DTEditText etFrom;
    @InjectView(R.id.et_live_in)
    protected DTEditText etLiveIn;

    private MenuItem logout;

    @Inject
    protected FragmentCompass fragmentCompass;

    private PickImageDialog pid;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        layoutConfiguration();
    }

    private void layoutConfiguration() {
        if (sv != null) {
            int minSideSize = getMinSideSize(getActivity());
            userCover.getLayoutParams().height = minSideSize;
        }
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
        logout = menu.findItem(R.id.item_logout);
        if (!getPresenter().isCurrentUserProfile()) {
            logout.setVisible(getPresenter().isCurrentUserProfile());
        }
    }

    @Override
    public void openAvatarPicker() {
        this.pid = new PickImageDialog(getActivity(), this);
        this.pid.setTitle(getString(R.string.profile_select_avatar_header));
        this.pid.setCallback(getPresenter().provideAvatarChooseCallback());
        this.pid.show();
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
    public void setLivesIn(String liveIn) {
        etLiveIn.setText(liveIn);
    }

    @OnClick(R.id.user_cover)
    public void onCoverClick() {
        getPresenter().coverClicked();
    }

    @Override
    public void openCoverPicker() {
        this.pid = new PickImageDialog(getActivity(), this);
        this.pid.setTitle(getString(R.string.profile_select_cover_header));
        this.pid.setCallback(getPresenter().provideCoverChooseCallback());
        this.pid.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.pid != null) {
            this.pid.onActivityResult(requestCode, resultCode, data);
        } else {
            Timber.w("Pick image dialog is null");
        }
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
