package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.SmartAvatarView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.feed.bundle.DescriptionBundle;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.feed.model.PostDescription;
import com.worldventures.dreamtrips.modules.feed.presenter.ActionEntityPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoPostCreationCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PostCreationTextCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.delegate.PhotoPostCreationDelegate;
import com.worldventures.dreamtrips.modules.feed.view.util.PhotoPostCreationItemDecorator;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoTagsBundle;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import timber.log.Timber;

import static com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoTagsBundle.PhotoEntity;

public abstract class ActionEntityFragment<PM extends ActionEntityPresenter, P extends Parcelable> extends RxBaseFragmentWithArgs<PM, P> implements ActionEntityPresenter.View, PhotoPostCreationDelegate {

   @Inject BackStackDelegate backStackDelegate;
   @Inject @ForActivity Provider<Injector> injectorProvider;

   @InjectView(R.id.avatar) protected SmartAvatarView avatar;
   @InjectView(R.id.name) protected TextView name;
   @InjectView(R.id.post_button) protected Button postButton;
   @InjectView(R.id.image) protected ImageView image;
   @InjectView(R.id.location) protected ImageView locationBtn;
   @InjectView(R.id.photos) protected RecyclerView photosList;

   BaseDelegateAdapter adapter;
   SweetAlertDialog dialog;
   PostDescription description = new PostDescription();

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      postButton.setText(getPostButtonText());
      //
      adapter = new BaseDelegateAdapter(getContext(), this);
      adapter.registerCell(PhotoCreationItem.class, PhotoPostCreationCell.class);
      adapter.registerCell(PostDescription.class, PostCreationTextCell.class);
      adapter.registerDelegate(PostDescription.class, new PostCreationTextCell.Delegate() {
         @Override
         public void onCellClicked(PostDescription model) {
            router.moveTo(Route.PHOTO_CREATION_DESC, NavigationConfigBuilder.forActivity()
                  .data(new DescriptionBundle(model.getDescription()))
                  .build());
         }
      });
      adapter.registerDelegate(PhotoCreationItem.class, this);
      LinearLayoutManager layout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
      photosList.setLayoutManager(layout);
      photosList.addItemDecoration(new PhotoPostCreationItemDecorator());
      photosList.setAdapter(adapter);
   }

   @Override
   public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      if (getActivity() instanceof MainActivity) {
         ((MainActivity) getActivity()).disableLeftDrawer();
      }
   }

   @Override
   public void onDestroyView() {
      super.onDestroyView();
      if (getActivity() instanceof MainActivity) {
         ((MainActivity) getActivity()).enableLeftDrawer();
      }
   }

   @Override
   public void onResume() {
      super.onResume();
      backStackDelegate.setListener(this::onBack);
      updateLocationButtonState();
      getPresenter().invalidateDynamicViews();
   }

   @Override
   public void attachPhotos(List<PhotoCreationItem> images) {
      adapter.addItems(images);
      adapter.notifyDataSetChanged();
   }

   @Override
   public void attachPhoto(PhotoCreationItem image) {
      adapter.addItem(image);
      adapter.notifyDataSetChanged();
   }

   @Override
   public void updateLocationButtonState() {
      boolean isSelected = getPresenter().getLocation() != null && !TextUtils.isEmpty(getPresenter().getLocation()
            .getName());
      locationBtn.setSelected(isSelected);
      locationBtn.setVisibility(View.VISIBLE);
   }

   @Override
   public void onPause() {
      super.onPause();
      backStackDelegate.setListener(null);
      SoftInputUtil.hideSoftInputMethod(getActivity());
   }

   @Override
   public void setName(String userName) {
      name.setText(userName);
   }

   @Override
   public void setAvatar(User user) {
      avatar.setImageURI(Uri.parse(user.getAvatar().getThumb()));
      avatar.setup(user, injectorProvider.get());
   }

   @Override
   public void setText(String text) {
      if (!adapter.getItems().contains(description)) {
         adapter.addItem(description);
      }

      description.setDescription(text);
      adapter.notifyDataSetChanged();
   }

   @Override
   public void showCancelationDialog() {
      if (dialog == null || !dialog.isShowing()) {
         dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE).setTitleText(getString(R.string.app_name))
               .setContentText(getString(R.string.post_cancel_message))
               .setConfirmText(getString(R.string.social_add_friend_yes))
               .setCancelText(getString(R.string.social_add_friend_no))
               .setConfirmClickListener(sweetAlertDialog -> {
                  sweetAlertDialog.dismissWithAnimation();
                  dialog = null;
                  cancel();
               })
               .setCancelClickListener(SweetAlertDialog::dismissWithAnimation);
         dialog.show();
      }
   }

   @Override
   public void cancel() {
      if (dialog != null && dialog.isShowing()) dialog.dismiss();

      router.moveTo(getRoute(), NavigationConfigBuilder.forRemoval().fragmentManager(getFragmentManager()).build());
   }

   @Override
   public void enableButton() {
      postButton.setTextColor(getResources().getColor(R.color.bucket_detailed_text_color));
      postButton.setClickable(true);
   }

   @Override
   public void disableButton() {
      postButton.setTextColor(getResources().getColor(R.color.grey));
      postButton.setClickable(false);
   }

   @Override
   public void onPostError() {
      postButton.setEnabled(true);
   }

   @OnClick(R.id.post_button)
   void onPost() {
      postButton.setEnabled(false);
      getPresenter().post();
   }

   protected boolean onBack() {
      try {
         if (getChildFragmentManager().popBackStackImmediate()) return true;
      } catch (Exception e) {
         Timber.e(e, "OnBack error"); //for avoid application crash
      }
      //
      getPresenter().cancelClicked();
      return true;
   }

   @OnClick(R.id.location)
   void onLocation() {
      getPresenter().onLocationClicked();
   }

   @OnClick(R.id.close)
   void onClose() {
      getPresenter().cancelClicked();
   }

   @OnClick(R.id.content_layout)
   void onSpaceClicked() {
      if (ViewUtils.isTablet(getActivity())) getPresenter().cancelClicked();
   }

   @Override
   public void openLocation(Location location) {
      router.moveTo(Route.ADD_LOCATION, NavigationConfigBuilder.forFragment()
            .backStackEnabled(true)
            .fragmentManager(getChildFragmentManager())
            .containerId(R.id.additional_page_container)
            .data(location)
            .build());
   }

   @Override
   public void updateItem(PhotoCreationItem item) {
      adapter.notifyItemChanged(adapter.getItems().indexOf(item));
   }

   //////////////////////////////////////////
   // Cell callbacks
   //////////////////////////////////////////

   @Override
   public void onCellClicked(PhotoCreationItem model) {
      // nothing to do
   }

   @Override
   public void onTagIconClicked(PhotoCreationItem item) {
      openTagEditScreen(item, null);
   }

   @Override
   public void onSuggestionClicked(PhotoCreationItem item, PhotoTag suggestion) {
      openTagEditScreen(item, suggestion);
   }

   protected void openTagEditScreen(PhotoCreationItem item, PhotoTag activeSuggestion) {
      SoftInputUtil.hideSoftInputMethod(getView());

      PhotoEntity photoEntity = new PhotoEntity(item.getOriginUrl(), item.getFileUri());
      EditPhotoTagsBundle bundle = new EditPhotoTagsBundle();
      bundle.setPhoto(photoEntity);
      bundle.setActiveSuggestion(activeSuggestion);
      bundle.setPhotoTags(item.getCombinedTags());
      bundle.setSuggestions(item.getSuggestions());
      bundle.setRequestId(item.getId());
      router.moveTo(Route.EDIT_PHOTO_TAG_FRAGMENT, NavigationConfigBuilder.forFragment()
            .backStackEnabled(true)
            .fragmentManager(getChildFragmentManager())
            .containerId(R.id.additional_page_container)
            .data(bundle)
            .build());
   }

   @Override
   public void onRemoveClicked(PhotoCreationItem uploadTask) {

   }

   @Override
   public void onPhotoTitleFocusChanged(boolean hasFocus) {
      onTitleFocusChanged(hasFocus);
   }

   @Override
   public void onTagsChanged() {
      getPresenter().invalidateDynamicViews();
   }

   ///////////////////////////////////////////////////////////////

   protected void onTitleFocusChanged(boolean hasFocus) {

   }

   @Override
   public void onPhotoTitleChanged(String title) {
      getPresenter().invalidateDynamicViews();
   }

   protected abstract int getPostButtonText();

   protected abstract Route getRoute();

}
