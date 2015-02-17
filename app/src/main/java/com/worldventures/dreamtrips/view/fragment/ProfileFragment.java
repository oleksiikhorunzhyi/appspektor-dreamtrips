package com.worldventures.dreamtrips.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.presentation.ProfileFragmentPresentation;
import com.worldventures.dreamtrips.utils.AdobeTrackingHelper;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.utils.ViewUtils;
import com.worldventures.dreamtrips.utils.busevents.ScreenOrientationChangeEvent;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.view.custom.DTEditText;
import com.worldventures.dreamtrips.view.dialog.PickImageDialog;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

import static com.worldventures.dreamtrips.utils.ViewUtils.getMinSideSize;

@Layout(R.layout.fragment_profile)
@MenuResource(R.menu.profile_fragment)
public class ProfileFragment extends BaseFragment<ProfileFragmentPresentation>
        implements DatePickerDialog.OnDateSetListener, View.OnTouchListener, ProfileFragmentPresentation.View {

    @InjectView(R.id.user_cover)
    ImageView userCover;

    @Optional
    @InjectView(R.id.vg_content_container)
    ViewGroup vgContentContainer;
    @InjectView(R.id.user_photo)
    ImageView userPhoto;
    @Optional
    @InjectView(R.id.user_photo_2)
    ImageView userPhoto2;
    @Optional
    @InjectView(R.id.user_photo_3)
    ImageView userPhoto3;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.user_email)
    TextView userEmail;
    @InjectView(R.id.et_date_of_birth)
    DTEditText dateOfBirth;
    @InjectView(R.id.pb)
    ProgressBarCircularIndeterminate progressBar;
    @Inject
    UniversalImageLoader universalImageLoader;
    @Optional
    @InjectView(R.id.sv)
    ScrollView sv;
    @InjectView(R.id.et_user_id)
    DTEditText etUserId;
    @InjectView(R.id.et_from)
    DTEditText etFrom;
    @InjectView(R.id.et_live_in)
    DTEditText etLiveIn;

    @Optional
    @InjectView(R.id.v_top_strip)
    View vTopStirp;

    private PickImageDialog pid;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        AdobeTrackingHelper.profile();
        layoutConfiguration();
    }

    public void onEvent(ScreenOrientationChangeEvent event) {
        layoutConfiguration();
    }

    private void layoutConfiguration() {
        if (vgContentContainer != null && sv != null && vTopStirp != null) {
            int minSideSize = getMinSideSize(getActivity());
            userCover.getLayoutParams().height = minSideSize;
            vgContentContainer.getLayoutParams().width = minSideSize;
            int m = 0;
            if (!ViewUtils.isLandscapeOrientation(getActivity())
                    && minSideSize < ViewUtils.getScreenWidth(getActivity())) {
                m = getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material);
            }
            ((ViewGroup.MarginLayoutParams) sv.getLayoutParams()).setMargins(0, m, 0, 0);
            vTopStirp.getLayoutParams().height = m;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!ViewUtils.isLandscapeOrientation(getActivity()))
            ((MainActivity) getActivity()).makeActionBarTransparent(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!ViewUtils.isLandscapeOrientation(getActivity()))
            ((MainActivity) getActivity()).makeActionBarTransparent(false);
    }

    @Override
    protected ProfileFragmentPresentation createPresentationModel(Bundle savedInstanceState) {
        return new ProfileFragmentPresentation(this);
    }

    @OnClick(R.id.user_photo)
    public void onPhotoClick(ImageView iv) {
        this.pid = new PickImageDialog(getActivity(), this);
        this.pid.setTitle("Select avatar");
        this.pid.setCallback(getPresentationModel().provideAvatarChooseCallback());
        this.pid.show();
    }

    @Override
    public void setAvatarImage(Uri uri) {
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> {
                if (uri != null) {
                    this.universalImageLoader.loadImage(uri, this.userPhoto, UniversalImageLoader.OP_AVATAR);
                }
            });
    }

    @Override
    public void setCoverImage(Uri uri) {
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> {
                if (uri != null) {
                    this.universalImageLoader.loadImage(uri, this.userCover, UniversalImageLoader.OP_COVER);
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
        this.pid.setTitle("Select cover");
        this.pid.setCallback(getPresentationModel().provideCoverChooseCallback());
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
                        getPresentationModel().logout();
                    }
                })
                .show();
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        getPresentationModel().onDataSet(year, month, day);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
            datePickerDialog.setYearRange(1915, 2015);
            datePickerDialog.show(getActivity().getSupportFragmentManager(), null);
        }
        return false;
    }
}
