package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.ActionBarTransparentEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeView;
import com.worldventures.dreamtrips.modules.profile.adapters.IgnoreFirstExpandedItemAdapter;
import com.worldventures.dreamtrips.modules.profile.presenter.AccountPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import io.techery.scalablecropp.library.Crop;

@Layout(R.layout.fragment_profile)
@MenuResource(R.menu.menu_empty)
public class AccountFragment extends ProfileFragment<AccountPresenter>
        implements AccountPresenter.View {

    public static final int AVATAR_CALLBACK = 1;
    public static final int COVER_CALLBACK = 2;

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
    protected BaseArrayListAdapter getAdapter() {
        return new IgnoreFirstExpandedItemAdapter(feedView.getContext(), injectorProvider);
    }

    @Override
    public void openAvatarPicker() {
        if (isVisibleOnScreen()) {
            getPresenter().setCallbackType(AVATAR_CALLBACK);
            showChooseSelectPhotoTypeDialog();
        }
    }

    @Override
    public void openCoverPicker() {
        if (isVisibleOnScreen()) {
            getPresenter().setCallbackType(COVER_CALLBACK);
            showChooseSelectPhotoTypeDialog();
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

    private void showChooseSelectPhotoTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle(R.string.select_photo)
                .setItems(R.array.photo_dialog_items, (dialogInterface, which) -> {
                    if (which == 0) {
                        getPresenter().pickImage(PickImageDelegate.REQUEST_CAPTURE_PICTURE);
                    } else if (which == 1) {
                        getPresenter().pickImage(PickImageDelegate.REQUEST_PICK_PICTURE);
                    } else {
                        getPresenter().pickImage(PickImageDelegate.REQUEST_FACEBOOK);
                    }
                });
        builder.show();
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
        if (!ViewUtils.isLandscapeOrientation(getActivity())) {
            if (getActivity() instanceof MainActivity) {
                profileToolbar.setNavigationIcon(R.drawable.ic_menu_hamburger);
                profileToolbar.setNavigationOnClickListener(view ->
                                ((MainActivity) getActivity()).openLeftDrawer()
                );
            } else {
                profileToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                profileToolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
            }
        }
    }
}
