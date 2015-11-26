package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeView;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.profile.adapters.IgnoreFirstExpandedItemAdapter;
import com.worldventures.dreamtrips.modules.profile.presenter.AccountPresenter;

import butterknife.InjectView;
import io.techery.scalablecropp.library.Crop;

@Layout(R.layout.fragment_account)
@MenuResource(R.menu.menu_empty)
public class AccountFragment extends ProfileFragment<AccountPresenter>
        implements AccountPresenter.View {

    public static final int AVATAR_CALLBACK = 1;
    public static final int COVER_CALLBACK = 2;

    @InjectView(R.id.photo_picker)
    PhotoPickerLayout photoPickerLayout;

    @Override
    protected AccountPresenter createPresenter(Bundle savedInstanceState) {
        return new AccountPresenter();
    }


    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        profileToolbarTitle.setVisibility(View.INVISIBLE);
        profileToolbarUserStatus.setVisibility(View.INVISIBLE);
        profileToolbar.inflateMenu(R.menu.profile_fragment);

        photoPickerLayout.setup(this, false);
        photoPickerLayout.setOnDoneClickListener(chosenImages -> getPresenter().attachImage(chosenImages));

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
        TrackingHelper.viewMyProfileScreen();
    }

    @Override
    protected BaseArrayListAdapter createAdapter() {
        return new IgnoreFirstExpandedItemAdapter(feedView.getContext(), this);
    }

    @Override
    public void openAvatarPicker() {
        if (isVisibleOnScreen()) {
            getPresenter().setCallbackType(AVATAR_CALLBACK);
            photoPickerLayout.showPanel();
        }
    }

    @Override
    public void openCoverPicker() {
        if (isVisibleOnScreen()) {
            getPresenter().setCallbackType(COVER_CALLBACK);
            photoPickerLayout.showPanel();
        }
    }

    @Override
    public void updateBadgeCount(int count) {
        View view = feedView.findViewById(R.id.badge);
        if (view != null) {
            BadgeView badgeView = (BadgeView) view;
            if (count > 0) {
                badgeView.setVisibility(View.VISIBLE);
                badgeView.setText(String.valueOf(count));
            } else {
                badgeView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!Crop.onActivityResult(requestCode, resultCode, data, getPresenter()::onCoverCropped)) {
            super.onActivityResult(requestCode, resultCode, data);
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
                        TrackingHelper.logout();
                        getPresenter().logout();
                    }
                }).show();
    }

    @Override
    protected void initialToolbar() {
        if (getActivity() instanceof MainActivity && !ViewUtils.isLandscapeOrientation(getActivity())) {
            profileToolbar.setNavigationIcon(R.drawable.ic_menu_hamburger);
            profileToolbar.setNavigationOnClickListener(view ->
                            ((MainActivity) getActivity()).openLeftDrawer()
            );
        } else {
            profileToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            profileToolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
        }
    }

    @Override
    public void hidePhotoPicker() {
        photoPickerLayout.hidePanel();
    }
}
