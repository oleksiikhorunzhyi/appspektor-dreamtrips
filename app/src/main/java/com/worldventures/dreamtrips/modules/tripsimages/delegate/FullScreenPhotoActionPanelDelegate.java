package com.worldventures.dreamtrips.modules.tripsimages.delegate;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
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

   @InjectView(R.id.iv_image) ScaleImageView image;
   @InjectView(R.id.ll_global_content_wrapper) LinearLayout contentWrapper;
   @InjectView(R.id.ll_more_info) LinearLayout moreInfo;
   @InjectView(R.id.tv_title) TextView userName;
   @InjectView(R.id.tv_description) TextView description;
   @InjectView(R.id.tv_see_more) TextView seeMore;
   @InjectView(R.id.tv_location) TextView location;
   @InjectView(R.id.tv_date) TextView date;
   @InjectView(R.id.tv_likes_count) TextView likesCount;
   @InjectView(R.id.tv_comments_count) TextView commentsCount;
   @InjectView(R.id.iv_like) ImageView like;
   @InjectView(R.id.flag) FlagView flag;
   @InjectView(R.id.edit) ImageView edit;
   @InjectView(R.id.user_photo) SmartAvatarView userPhoto;

   private Context context;
   private User account;
   private ContentVisibilityListener contentVisibilityListener;
   private Injector injector;

   @State boolean isContentWrapperVisible = true;

   public void setup(Activity activity, View rootView, User account, Injector injector) {
      ButterKnife.inject(this, rootView);
      this.context = activity;
      this.account = account;
      this.injector = injector;

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
      setUsername(photo.getOwner().getUsernameWithCompany(context));
      setCommentCount(photo.getCommentsCount());
      setDescription(photo.getTitle());
      setLikeCount(photo.getLikesCount());
      if (photo.getLocation() != null) {
         setLocation(photo.getLocation().getName());
      }
      setDate(DateTimeUtils.convertDateToString(photo.getShotAt(), DateTimeUtils.FULL_SCREEN_PHOTO_DATE_FORMAT));
      setLiked(photo.isLiked());
      setUserPresence(photo.getOwner());
      if (photo.getOwner() != null)
      setUserPhoto(photo.getOwner().getAvatar().getMedium());

      User owner = photo.getOwner();
      boolean isAccountsPhoto = owner != null && account.getId() == owner.getId();
      flag.setVisibility(isAccountsPhoto ? View.GONE : View.VISIBLE);
      edit.setVisibility(isAccountsPhoto ? View.VISIBLE : View.GONE);
   }

   private void setUserPhoto(String fsPhoto) {
      userPhoto.setVisibility(TextUtils.isEmpty(fsPhoto) ? View.GONE : View.VISIBLE);
      userPhoto.setImageURI(Uri.parse(fsPhoto));
   }

   public void setDate(String dateString) {
      date.setVisibility(TextUtils.isEmpty(dateString) ? View.GONE : View.VISIBLE);
      date.setText(dateString);
   }

   public void setLocation(String locationString) {
      location.setVisibility(TextUtils.isEmpty(locationString) ? View.GONE : View.VISIBLE);
      location.setText(locationString);
   }

   public void setCommentCount(int count) {
      commentsCount.setText(context.getString(count == 1 ? R.string.comment_single : R.string.comments, count));
      commentsCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
   }

   public void setLikeCount(int count) {
      likesCount.setText(context.getString(count == 1 ? R.string.like : R.string.likes, count));
      likesCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
   }

   public void setUserPresence(User user) {
      userPhoto.setup(user, injector);
   }

   public void setDescription(String desc) {
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

   public void setUsername(Spanned titleSpanned) {
      userName.setText(titleSpanned);
   }

   public void setLiked(boolean isLiked) {
      like.setSelected(isLiked);
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
