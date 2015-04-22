package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.profile.presenter.ProfilePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.PickImageDialog;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

import static com.worldventures.dreamtrips.core.utils.ViewUtils.getMinSideSize;

@Layout(R.layout.fragment_profile)
@MenuResource(R.menu.profile_fragment)
public class ProfileFragment extends BaseFragment<ProfilePresenter>
        implements ProfilePresenter.View {

    @InjectView(R.id.user_cover)
    protected SimpleDraweeView userCover;

    @InjectView(R.id.user_photo)
    protected SimpleDraweeView userPhoto;
    @InjectView(R.id.user_name)
    protected TextView userName;
    @InjectView(R.id.user_email)
    protected TextView userEmail;
    @InjectView(R.id.et_date_of_birth)
    protected DTEditText dateOfBirth;
    @InjectView(R.id.pb)
    protected ProgressBarCircularIndeterminate progressBar;
    @Optional
    @InjectView(R.id.sv)
    protected ScrollView sv;
    @InjectView(R.id.et_user_id)
    protected DTEditText etUserId;
    @InjectView(R.id.et_from)
    protected DTEditText etFrom;
    @InjectView(R.id.et_live_in)
    protected DTEditText etLiveIn;

    @Inject
    protected FragmentCompass fragmentCompass;

    private PickImageDialog pid;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        layoutConfiguration();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fragmentCompass.pop();
        fragmentCompass.replace(Route.MY_PROFILE);
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
        if (!ViewUtils.isLandscapeOrientation(getActivity())) {
            ((MainActivity) getActivity()).makeActionBarTransparent(true);
        } else {
            ((MainActivity) getActivity()).makeActionBarTransparent(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).makeActionBarTransparent(false);
    }

    @Override
    protected ProfilePresenter createPresenter(Bundle savedInstanceState) {
        return new ProfilePresenter(this);
    }

    @OnClick(R.id.user_photo)
    public void onPhotoClick(ImageView iv) {
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
    public void setUserEmail(String email) {
        userEmail.setText(email);
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
    public void onCoverClick(ImageView iv) {
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
            Log.e(ProfileFragment.class.getSimpleName(), "Pid is NULL");
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
