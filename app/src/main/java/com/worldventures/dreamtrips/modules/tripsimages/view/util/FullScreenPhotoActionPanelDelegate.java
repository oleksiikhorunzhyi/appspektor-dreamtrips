package com.worldventures.dreamtrips.modules.tripsimages.view.util;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.StringRes;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.FlagView;
import com.worldventures.dreamtrips.modules.common.view.custom.SmartAvatarView;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.ScaleImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import icepick.State;

public class FullScreenPhotoActionPanelDelegate {

   @InjectView(R.id.iv_image) ScaleImageView ivImage;
   @InjectView(R.id.ll_global_content_wrapper) protected LinearLayout llContentWrapper;
   @InjectView(R.id.ll_more_info) protected LinearLayout llMoreInfo;
   @InjectView(R.id.tv_title) protected TextView tvUsername;
   @InjectView(R.id.tv_description) protected TextView tvDescription;
   @InjectView(R.id.tv_see_more) protected TextView tvSeeMore;
   @InjectView(R.id.tv_location) protected TextView tvLocation;
   @InjectView(R.id.textViewInspireMeTitle) protected TextView textViewInspireMeTitle;
   @InjectView(R.id.tv_date) protected TextView tvDate;
   @InjectView(R.id.tv_likes_count) protected TextView tvLikesCount;
   @InjectView(R.id.tv_comments_count) protected TextView tvCommentsCount;
   @InjectView(R.id.iv_like) protected ImageView ivLike;
   @InjectView(R.id.iv_share) protected ImageView ivShare;
   @InjectView(R.id.flag) protected FlagView flag;
   @InjectView(R.id.edit) protected ImageView edit;
   @InjectView(R.id.user_photo) protected SmartAvatarView civUserPhoto;
   @InjectView(R.id.checkBox) protected CheckBox checkBox;
   @InjectView(R.id.iv_comment) protected ImageView ivComment;

   Context context;
   User account;
   ContentVisibilityListener contentVisibilityListener;
   private Injector injector;

   @State boolean isContentWrapperVisible = true;

   public void setup(Activity activity, View rootView, User account, Injector injector) {
      ButterKnife.inject(this, rootView);
      this.context = activity;
      this.account = account;
      this.injector = injector;

      ivImage.setSingleTapListener(() -> {
         toggleContent();
         if (contentVisibilityListener != null) {
            contentVisibilityListener.onVisibilityChange();
         }
      });
      ivImage.setDoubleTapListener(this::hideContent);
      if (isContentWrapperVisible) {
         showContent();
      } else {
         hideContent();
      }
   }

   public void setContent(Photo photo) {
      setUsername(photo.getUser().getUsernameWithCompany(context));
      setCommentCount(photo.getFSCommentCount());
      setDescription(photo.getFSDescription());
      setLikeCount(photo.getFSLikeCount());
      setLocation(photo.getFSLocation());
      setDate(photo.getFSDate());
      setUserPhoto(photo.getFSUserPhoto());
      setLiked(photo.isLiked());
      setUserPresence(photo.getUser());

      User owner = photo.getOwner();
      boolean isAccountsPhoto = owner != null && account.getId() == owner.getId();
      flag.setVisibility(isAccountsPhoto ? View.GONE : View.VISIBLE);
      edit.setVisibility(isAccountsPhoto ? View.VISIBLE : View.GONE);
   }


   public void setUserPhoto(String fsPhoto) {
      if (TextUtils.isEmpty(fsPhoto)) {
         civUserPhoto.setVisibility(View.GONE);
      } else {
         civUserPhoto.setImageURI(Uri.parse(fsPhoto));
      }
   }

   public void setDate(String date) {
      if (TextUtils.isEmpty(date)) {
         tvDate.setVisibility(View.GONE);
      } else {
         tvDate.setVisibility(View.VISIBLE);
         tvDate.setText(date);
      }
   }

   public void setLocation(String location) {
      if (TextUtils.isEmpty(location)) {
         tvLocation.setVisibility(View.GONE);
      } else {
         tvLocation.setVisibility(View.VISIBLE);
         tvLocation.setText(location);
      }
   }

   public void setCommentCount(int count) {
      if (count > 0) {
         @StringRes int commentFormat = count == 1 ? R.string.comment_single : R.string.comments;
         tvCommentsCount.setText(context.getString(commentFormat, count));
         tvCommentsCount.setVisibility(View.VISIBLE);
      } else {
         tvCommentsCount.setVisibility(View.GONE);
      }
   }

   public void setLikeCount(int count) {
      if (count > 0) {
         @StringRes int likesFormat = count == 1 ? R.string.like : R.string.likes;
         tvLikesCount.setText(context.getString(likesFormat, count));
         tvLikesCount.setVisibility(View.VISIBLE);
      } else {
         tvLikesCount.setVisibility(View.GONE);
      }
   }

   public void setUserPresence(User user) {
      civUserPhoto.setup(user, injector);
   }

   public void setDescription(String desc) {
      if (TextUtils.isEmpty(desc)) {
         tvDescription.setVisibility(View.GONE);
      } else {
         tvDescription.setText(desc);
         tvDescription.setVisibility(View.VISIBLE);
      }
      actionSeeMore();
   }


   @OnClick(R.id.tv_see_more)
   public void actionSeeMore() {
      llMoreInfo.setVisibility(View.VISIBLE);
      tvDescription.setSingleLine(false);

      tvSeeMore.setVisibility(View.GONE);
   }

   @OnClick({R.id.bottom_container, R.id.title_container})
   public void actionSeeLess() {
      llMoreInfo.setVisibility(View.GONE);
      tvDescription.setSingleLine(true);
      tvDescription.setVisibility(View.VISIBLE);
      tvSeeMore.setVisibility(View.VISIBLE);
   }

   public void setUsername(Spanned titleSpanned) {
      tvUsername.setText(titleSpanned);
   }

   public void setLiked(boolean isLiked) {
      ivLike.setSelected(isLiked);
   }


   public void hideContent() {
      llContentWrapper.setVisibility(View.GONE);
      isContentWrapperVisible = false;
   }

   public void showContent() {
      llContentWrapper.setVisibility(View.VISIBLE);
      isContentWrapperVisible = true;
   }

   public void toggleContent() {
      if (llContentWrapper.getVisibility() == View.VISIBLE) {
         hideContent();
      } else {
         showContent();
      }
   }

   public boolean isContentWrapperShown() {
      return isContentWrapperVisible;
   }

   public void setContentVisibilityListener(ContentVisibilityListener contentVisibilityListener) {
      this.contentVisibilityListener = contentVisibilityListener;
   }

   public interface ContentVisibilityListener {
      void onVisibilityChange();
   }
}
