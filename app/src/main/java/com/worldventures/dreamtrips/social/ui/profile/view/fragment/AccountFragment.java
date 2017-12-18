package com.worldventures.dreamtrips.social.ui.profile.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.core.model.User;
import com.worldventures.core.modules.picker.helper.PickerPermissionChecker;
import com.worldventures.core.modules.picker.helper.PickerPermissionUiHandler;
import com.worldventures.core.modules.picker.view.dialog.MediaPickerDialog;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.ui.util.permission.PermissionUtils;
import com.worldventures.core.ui.view.custom.BadgeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.activity.FeedActivity;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.delegate.UploadingCellDelegate;
import com.worldventures.dreamtrips.social.ui.profile.presenter.AccountPresenter;
import com.worldventures.dreamtrips.util.SocialCropImageManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@Layout(R.layout.fragment_account)
@MenuResource(R.menu.menu_empty)
public class AccountFragment extends ProfileFragment<AccountPresenter> implements AccountPresenter.View {

   @Inject PickerPermissionUiHandler pickerPermissionUiHandler;
   @Inject PermissionUtils permissionUtils;

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
         if (item.getItemId() == R.id.item_logout) {
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
      startAutoplayVideos();
   }

   @Override
   public void refreshFeedItems(List<FeedItem> items, UploadingPostsList uploadingPostsList, User user) {
      List newItems = new ArrayList();
      newItems.add(user);
      if (!uploadingPostsList.getPhotoPosts().isEmpty()) {
         newItems.add(uploadingPostsList);
      }
      newItems.addAll(items);
      fragmentWithFeedDelegate.updateItems(newItems, statePaginatedRecyclerViewManager.getStateRecyclerView());
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
   public void showPermissionDenied(String[] permissions) {
      if (permissionUtils.equals(permissions, PickerPermissionChecker.PERMISSIONS)) {
         pickerPermissionUiHandler.showPermissionDenied(getView());
      }
   }

   @Override
   public void showPermissionExplanationText(String[] permissions) {
      if (permissionUtils.equals(permissions, PickerPermissionChecker.PERMISSIONS)) {
         pickerPermissionUiHandler.showRational(getContext(), answer -> getPresenter().recheckPermission(permissions, answer));
      }
   }

   @Override
   public void showMediaPicker() {
      final MediaPickerDialog mediaPickerDialog = new MediaPickerDialog(getContext());
      mediaPickerDialog.setOnDoneListener(pickerAttachment -> getPresenter().imageSelected(pickerAttachment));
      mediaPickerDialog.show(1);
   }

   @Override
   public void cropImage(SocialCropImageManager socialCropImageManager, String path) {
      socialCropImageManager.cropImage(getActivity(), this, path);
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (!getPresenter().onActivityResult(requestCode, resultCode, data)) {
         super.onActivityResult(requestCode, resultCode, data);
      }
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
