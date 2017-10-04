package com.worldventures.dreamtrips.social.ui.tripsimages.delegate;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.social.ui.flags.view.FlagView;
import com.worldventures.dreamtrips.modules.common.view.custom.SmartAvatarView;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MediaActionPanelInfoInjector {

   @InjectView(R.id.user_photo) SmartAvatarView userPhoto;
   @InjectView(R.id.tv_title) TextView userName;
   @InjectView(R.id.tv_date) TextView date;
   @InjectView(R.id.tv_likes_count) TextView likesCount;
   @InjectView(R.id.tv_comments_count) TextView commentsCount;
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
      if (user == null) return;

      String photo = user.getAvatar().getMedium();
      userPhoto.setVisibility(TextUtils.isEmpty(photo) ? View.GONE : View.VISIBLE);
      userPhoto.setImageURI(Uri.parse(photo));

      userName.setText(user.getUsernameWithCompany(context));

      userPhoto.setup(user, injector);
   }

   public void setPublishedAtDate(Date photoDate) {
      if (photoDate == null) return;
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
}
