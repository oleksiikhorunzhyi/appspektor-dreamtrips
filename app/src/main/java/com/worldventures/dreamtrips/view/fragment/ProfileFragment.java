package com.worldventures.dreamtrips.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.utils.ViewIUtils;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.view.custom.DTEditText;
import com.worldventures.dreamtrips.view.dialog.PickImageDialog;
import com.worldventures.dreamtrips.presentation.ProfileFragmentPresentation;

import org.robobinding.ViewBinder;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_profile)
public class ProfileFragment extends BaseFragment<ProfileFragmentPresentation>
        implements DatePickerDialog.OnDateSetListener, View.OnTouchListener, ProfileFragmentPresentation.View {

    @InjectView(R.id.user_cover)
    ImageView userCover;
    @InjectView(R.id.user_photo)
    ImageView userPhoto;
    @InjectView(R.id.user_photo_2)
    ImageView userPhoto2;
    @InjectView(R.id.user_photo_3)
    ImageView userPhoto3;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.user_email)
    TextView userEmail;
    @InjectView(R.id.et_date_of_birth)
    DTEditText dateOfBirth;
    @Inject
    UniversalImageLoader universalImageLoader;
    private PickImageDialog pid;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        getPresentationModel().onViewCreated();

        ViewGroup.LayoutParams lp = userCover.getLayoutParams();
        lp.height = ViewIUtils.getScreenWidth(getActivity());//but by material style guide 3:2

    }

    @Override
    protected ProfileFragmentPresentation createPresentationModel(Bundle savedInstanceState) {
        return new ProfileFragmentPresentation(this);
    }

    @OnClick(R.id.user_photo)
    public void onPhotoClick(ImageView iv) {
        pid = new PickImageDialog(getActivity(), this);
        pid.setTitle("Select avatar");
        pid.setCallback(getPresentationModel().provideAvatarChooseCallback());
        pid.show();
    }

    @Override
    public void setAvatarImage(Uri uri) {
        getActivity().runOnUiThread(() -> {
            if (uri != null) {
                universalImageLoader.loadImage(uri, userPhoto, UniversalImageLoader.OP_AVATAR);
            }
        });
    }

    @Override
    public void setCoverImage(Uri uri) {
        getActivity().runOnUiThread(() -> {
            if (uri != null) {
                universalImageLoader.loadImage(uri, userCover, UniversalImageLoader.OP_COVER);
            }
        });
    }


    @OnClick(R.id.user_cover)
    public void onCoverClick(ImageView iv) {
        pid = new PickImageDialog(getActivity(), this);
        pid.setTitle("Select cover");
        pid.setCallback(getPresentationModel().provideCoverChooseCallback());
        pid.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        pid.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //TODO:fix action bar transparency
//        getActivity().makeActionBarTransparent(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_fragment, menu);
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

    @OnClick(R.id.btn_save)
    public void onSaveClick() {
        informUser("This feature is not implemented yet.");
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
