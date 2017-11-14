package com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.core.di.qualifier.ForActivity;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.model.ShareType;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.FragmentClassProviderModule;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.FragmentClassProvider;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.common.view.dialog.PhotosShareDialog;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.TranslateView;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.PhotoTagHolder;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.PhotoTagHolderManager;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.EditPhotoFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.popup.FeedItemMenuBuilder;
import com.worldventures.dreamtrips.social.ui.flags.view.FlagView;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.social.ui.share.bundle.ShareBundle;
import com.worldventures.dreamtrips.social.ui.share.view.ShareFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.delegate.FullScreenPhotoActionPanelDelegate;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Flag;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.SocialViewPagerState;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.FullscreenPhotoPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.EditPhotoBundle;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.custom.ImageryView;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import cn.pedant.SweetAlert.SweetAlertDialog;
import icepick.Icepick;

@Layout(R.layout.fragment_fullscreen_photo)
public class FullscreenPhotoFragment extends BaseFragmentWithArgs<FullscreenPhotoPresenter, Photo>
      implements Flaggable, FullScreenPhotoActionPanelDelegate.ContentVisibilityListener, FullscreenPhotoPresenter.View {

   @Inject Router router;
   @Inject @ForActivity Injector injector;
   @Inject @Named(FragmentClassProviderModule.PROFILE) FragmentClassProvider<Integer> fragmentClassProvider;
   @Inject SocialViewPagerState socialViewPagerState;

   @InjectView(R.id.tag) ImageView tag;
   @InjectView(R.id.flag) FlagView flag;
   @InjectView(R.id.iv_image) ImageryView imageryView;
   @InjectView(R.id.translate) TextView translateButton;
   @InjectView(R.id.taggable_holder) PhotoTagHolder photoTagHolder;
   @InjectView(R.id.translate_view) TranslateView viewWithTranslation;

   private FullScreenPhotoActionPanelDelegate viewDelegate = new FullScreenPhotoActionPanelDelegate();

   @Override
   protected FullscreenPhotoPresenter createPresenter(Bundle savedInstanceState) {
      return new FullscreenPhotoPresenter(getArgs());
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      viewDelegate.setup(getActivity(), rootView, getPresenter().getAccount(), injector);
      viewDelegate.setContentVisibilityListener(this);
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      Icepick.saveInstanceState(viewDelegate, outState);
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Icepick.restoreInstanceState(viewDelegate, savedInstanceState);
   }

   @Override
   public void setPhoto(Photo photo) {
      viewDelegate.setContent(photo);
      imageryView.loadImage(photo.getImagePath());
      imageryView.setOnErrorAction(getPresenter()::handleError);
      imageryView.setOnFinalImageSetAction(this::syncState);
   }

   @Override
   public void editPhoto(EditPhotoBundle bundle) {
      router.moveTo(EditPhotoFragment.class, NavigationConfigBuilder.forRemoval()
            .containerId(R.id.container_details_floating)
            .fragmentManager(getFragmentManager())
            .build());
      router.moveTo(EditPhotoFragment.class, NavigationConfigBuilder.forFragment()
            .containerId(R.id.container_details_floating)
            .backStackEnabled(false)
            .fragmentManager(getFragmentManager())
            .data(bundle)
            .build());
   }

   @Override
   public void showFlagDialog(List<Flag> flags) {
      hideFlagProgress();
      flag.showFlagsPopup(flags, (flagReasonId, reason) -> getPresenter().sendFlagAction(flagReasonId, reason));
   }

   @Override
   public void showFlagProgress() {
      flag.showProgress();
   }

   @Override
   public void hideFlagProgress() {
      flag.hideProgress();
   }

   @Override
   public void showContentWrapper() {
      socialViewPagerState.setTagHolderVisible(true);
      syncContentWrapperViewGroupWithGlobalState();
   }

   @OnClick({R.id.iv_comment, R.id.tv_comments_count})
   public void actionComment() {
      getPresenter().onCommentsAction();
   }

   @OnClick(R.id.tv_likes_count)
   public void actionLikes() {
      getPresenter().onCommentsAction();
   }

   @OnClick(R.id.iv_like)
   public void actionLike() {
      getPresenter().onLikeAction();
   }

   @OnClick(R.id.edit)
   public void actionEdit(View view) {
      view.setEnabled(false);
      FeedItemMenuBuilder.create(getActivity(), view, R.menu.menu_feed_entity_edit)
            .onDelete(this::deletePhoto)
            .onEdit(() -> {
               if (isVisibleOnScreen()) {
                  getPresenter().onEdit();
               }
            })
            .dismissListener(menu -> view.setEnabled(true))
            .show();
   }

   @OnClick(R.id.user_photo)
   void onUserClicked() {
      getPresenter().onUserClicked();
   }

   @OnClick(R.id.flag)
   public void actionFlag() {
      getPresenter().onFlagAction(this);
   }

   @OnClick(R.id.tag)
   public void onTag() {
      socialViewPagerState.setTagHolderVisible(!photoTagHolder.isShown());
      syncState();
   }

   private void deletePhoto() {
      getPresenter().onDelete();
      Dialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE).setTitleText(getResources().getString(R.string.photo_delete))
            .setContentText(getResources().getString(R.string.photo_delete_caption))
            .setConfirmText(getResources().getString(R.string.post_delete_confirm))
            .setConfirmClickListener(alertDialog -> {
               alertDialog.dismissWithAnimation();
               getPresenter().onDeleteAction();
            });
      dialog.setCanceledOnTouchOutside(true);
      dialog.show();
   }

   @Override
   public void onVisibilityChange() {
      socialViewPagerState.setContentWrapperVisible(!socialViewPagerState.isContentWrapperVisible());
      syncState();
   }

   private void syncState() {
      if (isResumed()) {
         getPresenter().shouldSyncTags();
         syncContentWrapperViewGroupWithGlobalState();
      }
   }

   private void syncContentWrapperViewGroupWithGlobalState() {
      if (socialViewPagerState.isContentWrapperVisible()) {
         viewDelegate.showContent();
      } else {
         viewDelegate.hideContent();
      }
   }

   @Override
   public void syncTagViewGroupWithGlobalState(Photo photo) {
      PhotoTagHolderManager photoTagHolderManager = new PhotoTagHolderManager(photoTagHolder,
            getPresenter().getAccount(), photo.getOwner());
      if (socialViewPagerState.isTagHolderVisible() && !photo.getPhotoTags().isEmpty()) {
         photoTagHolder.removeAllViews();
         tag.setSelected(true);
         photoTagHolderManager.show(imageryView);
         imageryView.setScaleEnabled(false);
         photoTagHolderManager.addExistsTagViews(photo.getPhotoTags());
         photoTagHolderManager.setTagDeletedListener(photoTag -> getPresenter().deleteTag(photoTag));
      } else {
         tag.setSelected(false);
         photoTagHolderManager.hide();
         imageryView.setScaleEnabled(true);
      }

      if (photo.getPhotoTagsCount() == 0) {
         tag.setVisibility(View.GONE);
      } else {
         tag.setVisibility(View.VISIBLE);
      }
   }

   @Override
   public void flagSentSuccess() {
      informUser(R.string.flag_sent_success_msg);
   }

   @OnClick(R.id.translate)
   void onTranlsate() {
      getPresenter().onTranslateClicked();
   }

   @Override
   public void showTranslation(String translation, String language) {
      viewWithTranslation.showTranslation(translation, language);
      translateButton.setVisibility(View.GONE);
   }

   @Override
   public void showTranslationInProgress() {
      viewWithTranslation.showProgress();
      translateButton.setVisibility(View.GONE);
   }

   @Override
   public void showTranslationButton() {
      viewWithTranslation.hide();
      translateButton.setVisibility(View.VISIBLE);
   }

   @Override
   public void hideTranslationButton() {
      viewWithTranslation.hide();
      translateButton.setVisibility(View.GONE);
   }


   @Override
   public void openShare(String imageUrl, String text, @ShareType String type) {
      ShareBundle data = new ShareBundle();
      data.setImageUrl(imageUrl);
      data.setText(text == null ? "" : text);
      data.setShareType(type);
      router.moveTo(ShareFragment.class, NavigationConfigBuilder.forActivity().data(data).build());
   }

   @Override
   public void openUser(UserBundle bundle) {
      router.moveTo(fragmentClassProvider.provideFragmentClass(bundle.getUser().getId()), NavigationConfigBuilder.forActivity()
            .data(bundle)
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .build());
   }

   @Optional
   @OnClick(R.id.iv_share)
   public void actionShare() {
      getPresenter().onShareAction();
   }

   @Override
   public void onShowShareOptions() {
      new PhotosShareDialog(getActivity(), type -> getPresenter().onShareOptionChosen(type)).show();
   }
}
