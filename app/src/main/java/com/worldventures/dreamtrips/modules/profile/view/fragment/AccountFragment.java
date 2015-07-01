package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.events.ActionBarTransparentEvent;
import com.worldventures.dreamtrips.modules.profile.presenter.AccountPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.PickImageDialog;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.scalablecropp.library.Crop;

@Layout(R.layout.fragment_profile)
@MenuResource(R.menu.profile_fragment)
public class AccountFragment extends ProfileFragment<AccountPresenter>
        implements AccountPresenter.View {

    private static final int AVATAR_CALLBACK = 1;
    private static final int COVER_CALLBACK = 2;

    @InjectView(R.id.rovia_bucks)
    protected TextView roviaBucks;
    @InjectView(R.id.dt_points)
    protected TextView dtPoints;

    private PickImageDialog pid;

    @Override
    protected AccountPresenter createPresenter(Bundle savedInstanceState) {
        return new AccountPresenter();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        cover.setVisibility(View.GONE);
        avatar.setVisibility(View.VISIBLE);
        addFriend.setVisibility(View.GONE);
        updateInfo.setVisibility(View.VISIBLE);
        userBalance.setVisibility(View.VISIBLE);
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

    @OnClick(R.id.user_photo)
    public void onPhotoClick() {
        getPresenter().photoClicked();
    }

    @OnClick(R.id.user_cover)
    public void onCoverClick() {
        getPresenter().coverClicked();
    }

    @Override
    public void openAvatarPicker() {
        this.pid = new PickImageDialog(getActivity(), this);
        this.pid.setTitle(getString(R.string.profile_select_avatar_header));
        this.pid.setCallback(getPresenter()::onAvatarChosen);
        this.pid.show();
        filePath = pid.getFilePath();
        callbackType = AVATAR_CALLBACK;
    }

    @Override
    public void openCoverPicker() {
        this.pid = new PickImageDialog(getActivity(), this);
        this.pid.setTitle(getString(R.string.profile_select_cover_header));
        this.pid.setCallback(getPresenter()::onCoverChosen);
        this.pid.show();
        filePath = pid.getFilePath();
        callbackType = COVER_CALLBACK;
    }

    @Override
    public void avatarProgressVisible(boolean visible) {
        progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void coverProgressVisible(boolean visible) {
        coverProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (Crop.onActivityResult(requestCode, resultCode, data, getPresenter()::onCoverCropped)) {
            return;
        }
        if (pid == null) {
            this.pid = new PickImageDialog(getActivity(), this);
            if (callbackType == AVATAR_CALLBACK)
                this.pid.setCallback(getPresenter()::onAvatarChosen);
            else if (callbackType == COVER_CALLBACK)
                this.pid.setCallback(getPresenter()::onCoverChosen);
            this.pid.setFilePath(filePath);
        }
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
    public void setRoviaBucks(String count) {
        roviaBucks.setText(Html.fromHtml(getString(R.string.profile_rovia_bucks, count)));
    }

    @Override
    public void setDreamTripPoints(String count) {
        dtPoints.setText(Html.fromHtml(getString(R.string.profile_dt_points, count)));
    }

    @OnClick(R.id.update_info)
    void onUpdateInfo() {

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
