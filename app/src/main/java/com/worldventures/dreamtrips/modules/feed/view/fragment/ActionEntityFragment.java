package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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
import com.worldventures.dreamtrips.modules.common.service.ConfigurationInteractor;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.SmartAvatarView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.feed.bundle.DescriptionBundle;
import com.worldventures.dreamtrips.modules.feed.model.ImmutableVideoCreationModel;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.feed.model.PostDescription;
import com.worldventures.dreamtrips.modules.feed.model.VideoCreationModel;
import com.worldventures.dreamtrips.modules.feed.presenter.ActionEntityPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoPostCreationCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PostCreationTextCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.ResizeableCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.VideoPostCreationCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.delegate.PhotoPostCreationDelegate;
import com.worldventures.dreamtrips.modules.feed.view.custom.MediaItemAnimation;
import com.worldventures.dreamtrips.modules.feed.view.util.PhotoPostCreationItemDecorator;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.EditPhotoTagsBundle;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import timber.log.Timber;

import static com.worldventures.dreamtrips.modules.tripsimages.view.args.EditPhotoTagsBundle.PhotoEntity;

public abstract class ActionEntityFragment<PM extends ActionEntityPresenter, P extends Parcelable> extends RxBaseFragmentWithArgs<PM, P>
      implements ActionEntityPresenter.View, PhotoPostCreationDelegate {

   @Inject BackStackDelegate backStackDelegate;
   @Inject ConfigurationInteractor configurationInteractor;
   @Inject @ForActivity Provider<Injector> injectorProvider;

   @InjectView(R.id.avatar) protected SmartAvatarView avatar;
   @InjectView(R.id.name) protected TextView name;
   @InjectView(R.id.post_button) protected Button postButton;
   @InjectView(R.id.image) protected ImageView image;
   @InjectView(R.id.location) protected ImageView locationBtn;
   @InjectView(R.id.photos) protected RecyclerView photosList;
   @InjectView(R.id.post_container) ViewGroup postContainer;

   protected BaseDelegateAdapter adapter;
   protected SweetAlertDialog dialog;
   protected PostDescription description = new PostDescription();
   protected LinearLayoutManager layoutManager;
   protected Pair<Integer, Integer> checkedRange = new Pair<>(-1, -1);

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      postButton.setText(getPostButtonText());
      adapter = new BaseDelegateAdapter(getContext(), this);
      adapter.registerCell(PhotoCreationItem.class, PhotoPostCreationCell.class);//Tag
      adapter.registerCell(PostDescription.class, PostCreationTextCell.class);//hashtag
      adapter.registerCell(ImmutableVideoCreationModel.class, VideoPostCreationCell.class);
      PostCreationTextCell.Delegate delegate = model -> openPhotoCreationDescriptionDialog((PostDescription) model);
      adapter.registerDelegate(PostDescription.class, delegate);
      adapter.registerDelegate(PhotoCreationItem.class, this);
      layoutManager = new LinearLayoutManager(getContext());
      photosList.setLayoutManager(layoutManager);
      photosList.addItemDecoration(new PhotoPostCreationItemDecorator());
      photosList.setAdapter(adapter);
      photosList.setItemAnimator(new MediaItemAnimation(position -> photosList.scrollToPosition(position), getResources()
            .getDimension(R.dimen.photo_cell_title_height)));

      configurationInteractor.configurationActionPipe()
            .observe()
            .compose(bindUntilDropViewComposer())
            .map(state -> state.action.getResult())
            .subscribe(this::updateContainerOnOrientationChange);

      manageScrollingAndResizing();
   }

   private void manageScrollingAndResizing() {
      photosList.addOnScrollListener(new RecyclerView.OnScrollListener() {
         @Override
         public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

            for (int position = firstVisibleItemPosition; position <= lastVisibleItemPosition; position++) {
               View view = layoutManager.findViewByPosition(position);
               RecyclerView.ViewHolder holder = photosList.getChildViewHolder(view);

               if (holder instanceof ResizeableCell && (position < checkedRange.first || position > checkedRange.second)) {
                  ((ResizeableCell) holder).checkSize();
               }
            }

            checkedRange = new Pair(firstVisibleItemPosition, lastVisibleItemPosition);
         }});
   }

   protected void openPhotoCreationDescriptionDialog(PostDescription model) {
      router.moveTo(Route.PHOTO_CREATION_DESC, NavigationConfigBuilder.forActivity()
            .data(new DescriptionBundle(model.getDescription()))
            .build());
   }

   private void updateContainerOnOrientationChange(Configuration configuration) {
      Resources res = postContainer.getContext().getResources();
      ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) postContainer.getLayoutParams();
      params.bottomMargin = res.getDimensionPixelSize(R.dimen.post_spacing_vertical_bottom);
      int marginLeftRight = res.getDimensionPixelSize(R.dimen.post_spacing_horizontal);
      params.leftMargin = marginLeftRight;
      params.rightMargin = marginLeftRight;
      params.topMargin = res.getDimensionPixelSize(R.dimen.post_spacing_vertical_top);
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
   public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      checkedRange = new Pair<>(-1, -1);
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
      if (images.size() > 1) {
         adapter.addItems(images);
         adapter.notifyDataSetChanged();
      } else {
         attachMedia(images.get(0));
      }
   }

   @Override
   public void attachVideo(VideoCreationModel model) {
      attachMedia(model);
   }

   private void attachMedia(Object model) {
      int position = adapter.getCount();
      adapter.addItem(model);
      adapter.notifyItemInserted(position);
      photosList.scrollToPosition(position);
   }

   @Override
   public void removeVideo(VideoCreationModel model) {
      adapter.remove(model);
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
      disableButton();
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
   public void updatePhoto(PhotoCreationItem item) {
      for (int i = 0; i < photosList.getChildCount(); i++) {
         if (photosList.getChildViewHolder(photosList.getChildAt(i)) instanceof PhotoPostCreationCell) {
            PhotoPostCreationCell cell = (PhotoPostCreationCell) photosList.getChildViewHolder(photosList.getChildAt(i));
            if (cell.getModelObject().getId() == item.getId()) {
               cell.syncUIStateWithModel();
               return;
            }
         }
      }
   }

   //////////////////////////////////////////
   // Cell callbacks
   //////////////////////////////////////////

   @Override
   public void onCellClicked(PhotoCreationItem model) { }

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
   public void onRemoveClicked(PhotoCreationItem uploadTask) { }

   @Override
   public void onPhotoTitleFocusChanged(boolean hasFocus) {
      onTitleFocusChanged(hasFocus);
   }

   @Override
   public void onTagsChanged() {
      getPresenter().invalidateDynamicViews();
   }

   @Override
   public void onPhotoTitleChanged(String title) {
      getPresenter().invalidateDynamicViews();
   }

   protected void onTitleFocusChanged(boolean hasFocus) { }

   protected abstract int getPostButtonText();

   protected abstract Route getRoute();

}
