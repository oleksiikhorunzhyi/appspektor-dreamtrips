package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.ActionBarTransparentEvent;
import com.worldventures.dreamtrips.core.utils.events.ActivityResult;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.profile.presenter.AccountPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import icepick.Icicle;
import io.techery.scalablecropp.library.Crop;

@Layout(R.layout.fragment_profile)
@MenuResource(R.menu.menu_empty)
public class AccountFragment extends ProfileFragment<AccountPresenter>
        implements AccountPresenter.View {

    private static final int AVATAR_CALLBACK = 1;
    private static final int COVER_CALLBACK = 2;

    @Icicle
    String filePath;
    @Icicle
    int callbackType;
    @Icicle
    int pidType;

    private PickImageDelegate pickImageDelegate;

    WeakHandler handler = new WeakHandler();

    @Override
    protected AccountPresenter createPresenter(Bundle savedInstanceState) {
        return new AccountPresenter();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        profileView.getControlPanel().setVisibility(View.VISIBLE);

        profileView.getCover().setVisibility(View.VISIBLE);
        profileView.getAvatar().setVisibility(View.VISIBLE);
        profileView.getAddFriend().setVisibility(View.GONE);
        profileView.getUpdateInfo().setVisibility(View.VISIBLE);
        profileView.getUserBalance().setVisibility(View.VISIBLE);

        pickImageDelegate = new PickImageDelegate(this);
        pickImageDelegate.setRequestType(callbackType);
        pickImageDelegate.setFilePath(filePath);

        pickImageDelegate.setErrorCallback(this::informUser);


        profileView.setOnPhotoClick(() -> getPresenter().photoClicked());
        profileView.setOnCoverClick(() -> getPresenter().coverClicked());

        profileToolbarTitle.setVisibility(View.INVISIBLE);
        profileToolbarUserStatus.setVisibility(View.INVISIBLE);
        profileToolbar.inflateMenu(R.menu.profile_fragment);
        boolean isMainActivity = getActivity() instanceof MainActivity;
        if (!ViewUtils.isLandscapeOrientation(getActivity())) {
            profileToolbar.setNavigationIcon(isMainActivity ? R.drawable.ic_menu_hamburger : R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            profileToolbar.setNavigationOnClickListener(view -> {
                if (isMainActivity) {
                    ((MainActivity) getActivity()).openLeftDrawer();
                } else {
                    getActivity().onBackPressed();
                }
            });
        }

        profileToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_logout:
                    showLogoutDialog();
            }
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        eventBus.post(new ActionBarTransparentEvent(true));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        eventBus.post(new ActionBarTransparentEvent(false));
    }

    @Override
    public void openAvatarPicker() {
        pickImageDelegate.setImageCallback(chosenImage -> getPresenter().onAvatarChosen(chosenImage[0]));
        callbackType = AVATAR_CALLBACK;
        showChooseSelectPhotoTypeDialog();
    }

    @Override
    public void openCoverPicker() {
        pickImageDelegate.setImageCallback(chosenImage -> getPresenter().onCoverChosen(chosenImage[0]));
        callbackType = COVER_CALLBACK;
        showChooseSelectPhotoTypeDialog();
    }

    private void showChooseSelectPhotoTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle(R.string.select_photo)
                .setItems(R.array.photo_dialog_items, (dialogInterface, which) -> {
                    if (which == 0) {
                        pidType = PickImageDelegate.REQUEST_CAPTURE_PICTURE;
                    } else if (which == 1) {
                        pidType = PickImageDelegate.REQUEST_PICK_PICTURE;
                    } else {
                        pidType = PickImageDelegate.REQUEST_FACEBOOK;
                    }

                    pickImageDelegate.setRequestType(pidType);
                    pickImageDelegate.show();
                    filePath = pickImageDelegate.getFilePath();
                });
        builder.show();
    }

    @Override
    public void avatarProgressVisible(boolean visible) {
        profileView.getProgressBar().setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void coverProgressVisible(boolean visible) {
        profileView.getCoverProgressBar().setVisibility(visible ? View.VISIBLE : View.GONE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!Crop.onActivityResult(requestCode, resultCode, data, getPresenter()::onCoverCropped)) {
            eventBus.postSticky(new ActivityResult(requestCode, resultCode, data));
        }
    }

    public void onEvent(ActivityResult event) {
        eventBus.removeStickyEvent(event);
        handler.post(() -> {
            if (pidType != 0) {
                pickImageDelegate.setRequestType(pidType);
                pickImageDelegate.setFilePath(filePath);

                if (callbackType == AVATAR_CALLBACK)
                    pickImageDelegate.setImageCallback(chosenImage -> getPresenter().onAvatarChosen(chosenImage[0]));
                else if (callbackType == COVER_CALLBACK)
                    pickImageDelegate.setImageCallback(chosenImage -> getPresenter().onCoverChosen(chosenImage[0]));

                pickImageDelegate.onActivityResult(event.requestCode,
                        event.resultCode, event.data);

                pidType = 0;
            }
        });
    }


    @Override
    public void setRoviaBucks(String count) {
        profileView.getRoviaBucks().setText(Html.fromHtml(getString(R.string.profile_rovia_bucks, count)));
    }

    @Override
    public void setDreamTripPoints(String count) {
        profileView.getDtPoints().setText(Html.fromHtml(getString(R.string.profile_dt_points, count)));
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
                }).show();
    }
}
