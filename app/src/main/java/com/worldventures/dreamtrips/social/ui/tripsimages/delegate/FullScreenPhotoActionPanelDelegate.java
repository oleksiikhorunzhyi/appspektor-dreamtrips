package com.worldventures.dreamtrips.social.ui.tripsimages.delegate;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.worldventures.core.janet.Injector;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.custom.ScaleImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import icepick.State;

public class FullScreenPhotoActionPanelDelegate {

   @InjectView(R.id.iv_image) ScaleImageView image;
   @InjectView(R.id.ll_global_content_wrapper) LinearLayout contentWrapper;
   @InjectView(R.id.ll_more_info) LinearLayout moreInfo;
   @InjectView(R.id.tv_description) TextView description;
   @InjectView(R.id.tv_location) TextView location;
   @InjectView(R.id.tv_see_more) TextView seeMore;

   private Context context;
   private User account;
   private ContentVisibilityListener contentVisibilityListener;

   private MediaActionPanelInfoInjector mediaActionPanelInfoInjector = new MediaActionPanelInfoInjector();

   @State boolean isContentWrapperVisible = true;

   public void setup(Activity activity, View rootView, User account, Injector injector) {
      ButterKnife.inject(this, rootView);
      this.context = activity;
      this.account = account;

      mediaActionPanelInfoInjector.setup(context, rootView, injector);

      image.setSingleTapListener(() -> {
         toggleContent();
         if (contentVisibilityListener != null) {
            contentVisibilityListener.onVisibilityChange();
         }
      });
      image.setDoubleTapListener(this::hideContent);
      if (isContentWrapperVisible) {
         showContent();
      } else {
         hideContent();
      }
   }

   public void setContent(Photo photo) {
      User owner = photo.getOwner();
      mediaActionPanelInfoInjector.setOwner(owner);
      mediaActionPanelInfoInjector.setCommentCount(photo.getCommentsCount());
      setDescription(photo.getTitle());
      mediaActionPanelInfoInjector.setLikeCount(photo.getLikesCount());
      setLocation(photo.getLocation());
      mediaActionPanelInfoInjector.setPublishedAtDate(photo.getShotAt());
      mediaActionPanelInfoInjector.setLiked(photo.isLiked());
      boolean isAccountsPhoto = owner != null && account.getId() == owner.getId();
      mediaActionPanelInfoInjector.enableFlagging(!isAccountsPhoto);
      mediaActionPanelInfoInjector.enableEdit(isAccountsPhoto);
   }

   public void setLocation(Location photoLocation) {
      if (photoLocation == null) return;
      String locationString = photoLocation.getName();
      location.setVisibility(TextUtils.isEmpty(locationString) ? View.GONE : View.VISIBLE);
      location.setText(locationString);
   }

   private void setDescription(String desc) {
      description.setVisibility(TextUtils.isEmpty(desc) ? View.GONE : View.VISIBLE);
      description.setText(desc);
      actionSeeMore();
   }

   @OnClick(R.id.tv_see_more)
   public void actionSeeMore() {
      moreInfo.setVisibility(View.VISIBLE);
      description.setSingleLine(false);
      seeMore.setVisibility(View.GONE);
   }

   @OnClick({R.id.bottom_container, R.id.title_container})
   public void actionSeeLess() {
      moreInfo.setVisibility(View.GONE);
      description.setSingleLine(true);
      description.setVisibility(View.VISIBLE);
      seeMore.setVisibility(View.VISIBLE);
   }

   public void hideContent() {
      contentWrapper.setVisibility(View.GONE);
      isContentWrapperVisible = false;
   }

   public void showContent() {
      contentWrapper.setVisibility(View.VISIBLE);
      isContentWrapperVisible = true;
   }

   private void toggleContent() {
      if (contentWrapper.getVisibility() == View.VISIBLE) {
         hideContent();
      } else {
         showContent();
      }
   }

   public void setContentVisibilityListener(ContentVisibilityListener contentVisibilityListener) {
      this.contentVisibilityListener = contentVisibilityListener;
   }

   public interface ContentVisibilityListener {
      void onVisibilityChange();
   }
}