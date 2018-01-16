package com.worldventures.dreamtrips.social.ui.tripsimages.delegate;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.core.janet.Injector;
import com.worldventures.core.model.User;
import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.utils.UserUtils;
import com.worldventures.dreamtrips.social.ui.profile.view.widgets.SmartAvatarView;
import com.worldventures.dreamtrips.social.ui.flags.view.FlagView;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.functions.Action0;

public class MediaActionPanelInfoInjector {

   @InjectView(R.id.user_photo) SmartAvatarView userPhoto;
   @InjectView(R.id.tv_title) TextView userName;
   @InjectView(R.id.tv_date) TextView date;
   @InjectView(R.id.tv_likes_count) TextView likesCount;
   @InjectView(R.id.tv_comments_count) TextView commentsCount;
   @InjectView(R.id.iv_comment) ImageView commentButton;
   @InjectView(R.id.iv_like) ImageView like;
   @InjectView(R.id.flag) FlagView flag;
   @InjectView(R.id.edit) ImageView edit;

   private Context context;
   private Injector injector;

   public void setup(Context context, View rootView, Injector injector) {
      ButterKnife.inject(this, rootView);
      this.context = context;
      this.injector = injector;
   }

   public void enableEdit(boolean enable) {
      edit.setVisibility(enable ? View.VISIBLE : View.GONE);
   }

   public void enableFlagging(boolean enableFlag) {
      flag.setVisibility(enableFlag ? View.VISIBLE : View.GONE);
   }

   public void setOwner(User user) {
      if (user == null) {
         return;
      }

      String photo = user.getAvatar().getMedium();
      userPhoto.setVisibility(TextUtils.isEmpty(photo) ? View.GONE : View.VISIBLE);
      userPhoto.setImageURI(Uri.parse(photo));

      userName.setText(UserUtils.getUsernameWithCompany(context, user));

      userPhoto.setup(user, injector);
   }

   public void setPublishedAtDate(Date photoDate) {
      if (photoDate == null) {
         return;
      }
      String dateString = DateTimeUtils.convertDateToString(photoDate, DateTimeUtils.FULL_SCREEN_PHOTO_DATE_FORMAT);
      date.setVisibility(TextUtils.isEmpty(dateString) ? View.GONE : View.VISIBLE);
      date.setText(dateString);
   }

   public void setCommentCount(int count) {
      commentsCount.setText(context.getString(count == 1 ? R.string.comment_single : R.string.comments, count));
      commentsCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
   }

   public void setLikeCount(int count) {
      likesCount.setText(context.getString(count == 1 ? R.string.like : R.string.likes, count));
      likesCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
   }

   public void setLiked(boolean isLiked) {
      like.setSelected(isLiked);
   }

   public void setLikeAction(Action0 action) {
      like.setOnClickListener(view -> action.call());
   }

   public void setLikesCountAction(Action0 action) {
      likesCount.setOnClickListener(view -> action.call());
   }

   public void setCommentsCountAction(Action0 action) {
      commentsCount.setOnClickListener(view -> action.call());
   }

   public void setFlagAction(Action0 action) {
      flag.setOnClickListener(view -> action.call());
   }

   public void setEditAction(Action0 action) {
      edit.setOnClickListener(view -> action.call());
   }

   public void setCommentButtonAction(Action0 action) {
      commentButton.setOnClickListener(view -> action.call());
   }

   public ImageView getEditButton() {
      return edit;
   }
}
