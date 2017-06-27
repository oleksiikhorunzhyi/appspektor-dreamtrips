package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.delegate.SocialCropImageManager;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeView;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.modules.feed.view.activity.FeedActivity;
import com.worldventures.dreamtrips.modules.feed.view.cell.delegate.UploadingCellDelegate;
import com.worldventures.dreamtrips.modules.media_picker.bundle.PickerBundle;
import com.worldventures.dreamtrips.modules.profile.adapters.IgnoreFirstExpandedItemAdapter;
import com.worldventures.dreamtrips.modules.profile.presenter.AccountPresenter;

import java.util.ArrayList;
import java.util.List;

@Layout(R.layout.fragment_account)
@MenuResource(R.menu.menu_empty)
public class AccountFragment extends ProfileFragment<AccountPresenter> implements AccountPresenter.View {

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
   protected void registerCellDelegates() {
      super.registerCellDelegates();
      fragmentWithFeedDelegate.registerDelegate(UploadingPostsList.class, new UploadingCellDelegate(getPresenter(),
            getContext()));
   }

   @Override
   public void onResume() {
      super.onResume();
      TrackingHelper.viewMyProfileScreen();
      startAutoplayVideos();
   }

   @Override
   protected BaseDelegateAdapter createAdapter() {
      return new IgnoreFirstExpandedItemAdapter(getContext(), this);
   }

   @Override
   public void refreshFeedItems(List<FeedItem> items, UploadingPostsList uploadingPostsList) {
      List newItems = new ArrayList();
      if (!uploadingPostsList.getPhotoPosts().isEmpty()) {
         newItems.add(uploadingPostsList);
      }
      newItems.addAll(items);
      fragmentWithFeedDelegate.updateItems(items);
      startAutoplayVideos();
   }

   @Override
   public void openAvatarPicker() {
      if (isVisibleOnScreen()) {
         getPresenter().onAvatarClicked();
      }
   }

   @Override
   public void openCoverPicker() {
      if (isVisibleOnScreen()) {
         getPresenter().onCoverClicked();
      }
   }

   @Override
   public void updateBadgeCount(int count) {
      View view = getActivity().findViewById(R.id.badge);
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

   private void showLogoutDialog() {
      new MaterialDialog.Builder(getActivity()).title(getString(R.string.logout_dialog_title))
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

   @Override
   protected void initToolbar() {
      if (getActivity() instanceof FeedActivity && !ViewUtils.isLandscapeOrientation(getActivity())) {
         profileToolbar.setNavigationIcon(R.drawable.ic_menu_hamburger);
         profileToolbar.setNavigationOnClickListener(view -> ((FeedActivity) getActivity()).openLeftDrawer());
      } else {
         profileToolbar.setNavigationIcon(R.drawable.back_icon);
         profileToolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
      }
   }

   @Override
   public void showMediaPicker(int requestId) {
      router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forFragment()
            .backStackEnabled(false)
            .fragmentManager(getChildFragmentManager())
            .containerId(R.id.picker_container)
            .data(new PickerBundle.Builder().setRequestId(requestId).build())
            .build());
   }

   @Override
   public void hideMediaPicker() {
      router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forRemoval()
            .fragmentManager(getChildFragmentManager())
            .containerId(R.id.picker_container)
            .build());
   }

   @Override
   public void cropImage(SocialCropImageManager socialCropImageManager, String path) {
      socialCropImageManager.cropImage(this, path);
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (!getPresenter().onActivityResult(requestCode, resultCode, data))
         super.onActivityResult(requestCode, resultCode, data);
   }

   @Override
   public void onUserCoverClicked() {
      getPresenter().coverClicked();
   }

   @Override
   public void onUserPhotoClicked() {
      getPresenter().photoClicked();
   }
}
